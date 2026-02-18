package com.interview.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.FeedbackDTO;
import com.interview.platform.model.Feedback;
import com.interview.platform.model.Interview;
import com.interview.platform.model.Response;
import com.interview.platform.repository.FeedbackRepository;
import com.interview.platform.repository.InterviewRepository;
import com.interview.platform.repository.ResponseRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Service for generating AI-powered interview feedback using the OpenAI API.
 *
 * <h3>Purpose:</h3>
 * <p>
 * After a user completes an interview and all responses have been transcribed,
 * this service builds a prompt from the question-answer pairs and sends it to
 * OpenAI for evaluation. The AI returns a structured feedback JSON containing
 * an overall score, strengths, weaknesses, recommendations, and a detailed
 * analysis. This feedback is persisted to the database and displayed on the
 * user's dashboard.
 * </p>
 *
 * <h3>P1 Changes:</h3>
 * <ul>
 * <li><strong>Resilience4j:</strong> The manual retry loop with exponential
 * backoff for the OpenAI API call has been replaced with programmatic
 * Resilience4j {@link Retry} and {@link CircuitBreaker} decorators.
 * This provides configurable retry behavior, circuit breaker protection
 * against sustained OpenAI outages, and observable metrics via the
 * Spring Boot Actuator.</li>
 * <li><strong>SLF4J logging:</strong> Migrated from {@code java.util.logging}
 * to SLF4J for structured, parameterized logging consistent with the
 * rest of the application.</li>
 * </ul>
 *
 * <h3>Circuit Breaker Behavior:</h3>
 * <p>
 * This service shares the "openai" circuit breaker instance with
 * {@link OpenAIService}. If OpenAI is experiencing issues, both question
 * generation and feedback generation will fail fast, preventing unnecessary
 * API calls and reducing latency for the user. The
 * {@link com.interview.platform.task.InterviewRecoveryTask} will detect
 * interviews stuck in {@code PROCESSING} state and transition them to
 * {@code FAILED} after a configurable timeout.
 * </p>
 *
 * @see OpenAIService
 * @see com.interview.platform.config.ResilienceConfig
 * @see com.interview.platform.task.InterviewRecoveryTask
 */
@Service
public class AIFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(AIFeedbackService.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate openAIRestTemplate;
    private final ObjectMapper objectMapper;
    private final ResponseRepository responseRepository;
    private final InterviewRepository interviewRepository;
    private final FeedbackRepository feedbackRepository;

    // Resilience4j instances — shares the "openai" named instances with
    // OpenAIService
    private final Retry openaiRetry;
    private final CircuitBreaker openaiCircuitBreaker;

    @Value("${openai.model:gpt-4o}")
    private String model;

    @Value("${openai.max-tokens:2000}")
    private int maxTokens;

    @Value("${openai.temperature:0.7}")
    private double temperature;

    public AIFeedbackService(@Qualifier("openAIRestTemplate") RestTemplate openAIRestTemplate,
            ObjectMapper objectMapper,
            ResponseRepository responseRepository,
            InterviewRepository interviewRepository,
            FeedbackRepository feedbackRepository,
            @Qualifier("openaiRetry") Retry openaiRetry,
            @Qualifier("openaiCircuitBreaker") CircuitBreaker openaiCircuitBreaker) {
        this.openAIRestTemplate = openAIRestTemplate;
        this.objectMapper = objectMapper;
        this.responseRepository = responseRepository;
        this.interviewRepository = interviewRepository;
        this.feedbackRepository = feedbackRepository;
        this.openaiRetry = openaiRetry;
        this.openaiCircuitBreaker = openaiCircuitBreaker;
    }

    // ════════════════════════════════════════════════════════════════
    // Public API
    // ════════════════════════════════════════════════════════════════

    /**
     * Generate AI feedback for a completed interview (synchronous).
     *
     * @param interviewId the interview to generate feedback for
     * @return the persisted Feedback entity
     */
    @Transactional
    public Feedback generateFeedback(Long interviewId) {
        log.info("Generating AI feedback for interview ID: {}", interviewId);

        // Step 1: Fetch interview
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found: " + interviewId));

        // Step 2: Check if feedback already exists
        Optional<Feedback> existingFeedback = feedbackRepository.findByInterviewId(interviewId);
        if (existingFeedback.isPresent()) {
            log.info("Feedback already exists for interview ID: {}", interviewId);
            return existingFeedback.get();
        }

        // Step 3: Fetch all responses with transcriptions
        List<Response> responses = responseRepository.findByInterviewIdOrderByQuestionId(interviewId);
        if (responses.isEmpty()) {
            throw new RuntimeException("No responses found for interview: " + interviewId);
        }

        // Step 4: Build prompt from Q&A pairs
        String prompt = buildFeedbackPrompt(responses);

        // Step 5: Call OpenAI (with Resilience4j retry + circuit breaker)
        String aiResponse = callOpenAIWithResilience(prompt);

        // Step 6: Parse feedback
        FeedbackDTO feedbackDTO = parseFeedbackResponse(aiResponse);

        // Step 7: Create and save Feedback entity
        Feedback feedback = createFeedbackEntity(interview, feedbackDTO);

        // Step 8: Update interview with overall score
        interview.setOverallScore(feedbackDTO.getScore());
        interviewRepository.save(interview);

        log.info("AI feedback generated for interview ID: {} — score: {}", interviewId, feedbackDTO.getScore());

        return feedback;
    }

    /**
     * Async version for background processing.
     */
    @Async("avatarTaskExecutor")
    public CompletableFuture<Feedback> generateFeedbackAsync(Long interviewId) {
        try {
            Feedback feedback = generateFeedback(interviewId);
            return CompletableFuture.completedFuture(feedback);
        } catch (Exception e) {
            log.error("Async feedback generation failed for interview ID: {}", interviewId, e);
            CompletableFuture<Feedback> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Prompt Building
    // ════════════════════════════════════════════════════════════════

    private String buildFeedbackPrompt(List<Response> responses) {
        StringBuilder qaPairs = new StringBuilder();
        int questionNumber = 1;

        for (Response response : responses) {
            String questionText = response.getQuestion() != null
                    ? response.getQuestion().getQuestionText()
                    : "Question " + questionNumber;

            String transcription = response.getTranscription();
            if (transcription == null || transcription.isBlank()) {
                transcription = "[No response transcription available]";
            }

            qaPairs.append(String.format("[Q%d: %s, A%d: %s]\n",
                    questionNumber, questionText, questionNumber, transcription));
            questionNumber++;
        }

        return String.format(
                """
                        Analyze this interview performance based on the following Q&A pairs:
                        %s

                        Provide:
                        1. Overall score (0-100)
                        2. Top 3-5 strengths
                        3. Top 3-5 weaknesses
                        4. Specific actionable recommendations

                        Return as JSON only, no markdown:
                        {
                          "score": <number 0-100>,
                          "strengths": ["strength1", "strength2", ...],
                          "weaknesses": ["weakness1", "weakness2", ...],
                          "recommendations": ["recommendation1", "recommendation2", ...],
                          "detailedAnalysis": "A paragraph summarizing the overall performance"
                        }
                        """,
                qaPairs.toString());
    }

    // ════════════════════════════════════════════════════════════════
    // OpenAI API Call (with Resilience4j)
    // ════════════════════════════════════════════════════════════════

    /**
     * Call OpenAI Chat Completion API with Resilience4j retry and circuit breaker.
     *
     * <p>
     * Replaces the previous manual retry loop with exponential backoff.
     * The retry configuration (max attempts, backoff multiplier, retryable
     * exceptions) is defined in
     * {@link com.interview.platform.config.ResilienceConfig}
     * and can be tuned via application.properties.
     * </p>
     *
     * <p>
     * Shares the "openai" circuit breaker instance with {@link OpenAIService},
     * so failures in question generation and feedback generation are tracked
     * together. This is intentional — if OpenAI is down, both operations
     * should fail fast.
     * </p>
     *
     * @param prompt the feedback generation prompt
     * @return the assistant's response content (JSON feedback)
     * @throws RuntimeException if all retries are exhausted or circuit is open
     */
    private String callOpenAIWithResilience(String prompt) {
        return Decorators.ofSupplier(() -> callOpenAI(prompt))
                .withRetry(openaiRetry)
                .withCircuitBreaker(openaiCircuitBreaker)
                .decorate()
                .get();
    }

    /**
     * Make the actual HTTP POST to the OpenAI Chat Completion endpoint.
     *
     * <p>
     * This is the "raw" API call without any retry or circuit breaker
     * logic. It is wrapped by {@link #callOpenAIWithResilience} which
     * handles resilience concerns.
     * </p>
     *
     * @param prompt the feedback generation prompt
     * @return the assistant's response content (JSON feedback string)
     * @throws RuntimeException on API errors or empty responses
     */
    private String callOpenAI(String prompt) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are an expert interview coach and career advisor. "
                        + "Analyze interview responses and provide constructive, actionable feedback. "
                        + "Always respond with valid JSON only.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        log.debug("Calling OpenAI Chat Completion for feedback: model={}, maxTokens={}", model, maxTokens);
        ResponseEntity<String> response = openAIRestTemplate.postForEntity(
                OPENAI_API_URL, requestBody, String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Empty response from OpenAI API");
        }

        return extractContentFromResponse(response.getBody());
    }

    private String extractContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");

            if (choices.isEmpty() || choices.isMissingNode()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            String content = choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (content == null || content.isBlank()) {
                throw new RuntimeException("Empty content in OpenAI API response");
            }

            // Log token usage for monitoring and cost tracking
            JsonNode usage = root.path("usage");
            if (!usage.isMissingNode()) {
                log.info("Feedback OpenAI tokens — prompt: {}, completion: {}, total: {}",
                        usage.path("prompt_tokens").asInt(),
                        usage.path("completion_tokens").asInt(),
                        usage.path("total_tokens").asInt());
            }

            return content.trim();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse OpenAI response JSON", e);
            throw new RuntimeException("Failed to parse OpenAI API response", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Response Parsing
    // ════════════════════════════════════════════════════════════════

    private FeedbackDTO parseFeedbackResponse(String content) {
        try {
            // Strip markdown code fences if present
            String jsonContent = content;
            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.replaceAll("^```(?:json)?\\s*", "");
                jsonContent = jsonContent.replaceAll("\\s*```$", "");
            }
            jsonContent = jsonContent.trim();

            FeedbackDTO dto = objectMapper.readValue(jsonContent, FeedbackDTO.class);

            // Validate score range
            if (dto.getScore() < 0) {
                dto.setScore(0);
            } else if (dto.getScore() > 100) {
                dto.setScore(100);
            }

            // Ensure lists are not null
            if (dto.getStrengths() == null) {
                dto.setStrengths(Collections.emptyList());
            }
            if (dto.getWeaknesses() == null) {
                dto.setWeaknesses(Collections.emptyList());
            }
            if (dto.getRecommendations() == null) {
                dto.setRecommendations(Collections.emptyList());
            }
            if (dto.getDetailedAnalysis() == null) {
                dto.setDetailedAnalysis("");
            }

            return dto;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse feedback JSON: {}", content, e);
            throw new RuntimeException(
                    "Failed to parse AI feedback. The response was not in the expected JSON format.", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Entity Creation
    // ════════════════════════════════════════════════════════════════

    private Feedback createFeedbackEntity(Interview interview, FeedbackDTO dto) {
        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setUser(interview.getUser());
        feedback.setOverallScore(dto.getScore());

        // Store lists as JSON strings
        try {
            feedback.setStrengths(objectMapper.writeValueAsString(dto.getStrengths()));
            feedback.setWeaknesses(objectMapper.writeValueAsString(dto.getWeaknesses()));
            feedback.setRecommendations(objectMapper.writeValueAsString(dto.getRecommendations()));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize feedback lists to JSON, using toString()", e);
            feedback.setStrengths(dto.getStrengths().toString());
            feedback.setWeaknesses(dto.getWeaknesses().toString());
            feedback.setRecommendations(dto.getRecommendations().toString());
        }

        feedback.setDetailedAnalysis(dto.getDetailedAnalysis());

        return feedbackRepository.save(feedback);
    }

}
