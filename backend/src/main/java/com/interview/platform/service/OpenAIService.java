package com.interview.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.QuestionDTO;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service for generating interview questions using the OpenAI Chat Completions
 * API.
 *
 * <h3>Purpose:</h3>
 * <p>
 * Takes a candidate's resume text and target job role, and generates a set of
 * tailored interview questions (technical, behavioral, situational) using
 * OpenAI's
 * GPT model. The generated questions are returned as a list of
 * {@link QuestionDTO}
 * objects ready for persistence.
 * </p>
 *
 * <h3>P1 Changes:</h3>
 * <ul>
 * <li><strong>Resilience4j:</strong> The manual retry loop with exponential
 * backoff has been replaced with programmatic Resilience4j {@link Retry}
 * and {@link CircuitBreaker} decorators. This provides configurable retry
 * behavior, circuit breaker protection against sustained OpenAI outages,
 * and observable metrics via Spring Boot Actuator.</li>
 * <li><strong>SLF4J logging:</strong> Migrated from {@code java.util.logging}
 * to SLF4J for structured, parameterized logging consistent with the
 * rest of the application.</li>
 * </ul>
 *
 * <h3>Circuit Breaker Behavior:</h3>
 * <p>
 * When the OpenAI circuit breaker opens (due to sustained failures), calls to
 * {@link #generateInterviewQuestions} will fail fast with a
 * {@code CallNotPermittedException}. The caller ({@code InterviewService})
 * should
 * handle this by transitioning the interview to {@code FAILED} status and
 * informing the user that the service is temporarily unavailable.
 * </p>
 *
 * @see com.interview.platform.config.ResilienceConfig
 * @see AIFeedbackService
 */
@Service
public class OpenAIService {

    private static final Logger log = LoggerFactory.getLogger(OpenAIService.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate openAIRestTemplate;
    private final ObjectMapper objectMapper;

    // Resilience4j instances for OpenAI API calls
    private final Retry openaiRetry;
    private final CircuitBreaker openaiCircuitBreaker;

    @Value("${openai.model:gpt-4o}")
    private String model;

    @Value("${openai.max-tokens:2000}")
    private int maxTokens;

    @Value("${openai.temperature:0.7}")
    private double temperature;

    public OpenAIService(@Qualifier("openAIRestTemplate") RestTemplate openAIRestTemplate,
            ObjectMapper objectMapper,
            @Qualifier("openaiRetry") Retry openaiRetry,
            @Qualifier("openaiCircuitBreaker") CircuitBreaker openaiCircuitBreaker) {
        this.openAIRestTemplate = openAIRestTemplate;
        this.objectMapper = objectMapper;
        this.openaiRetry = openaiRetry;
        this.openaiCircuitBreaker = openaiCircuitBreaker;
    }

    /**
     * Generate interview questions based on a resume and job role.
     *
     * @param resumeText the extracted text content from the user's resume
     * @param jobRole    the target job role title
     * @return list of generated QuestionDTOs
     */
    public List<QuestionDTO> generateInterviewQuestions(String resumeText, String jobRole) {
        log.info("Generating interview questions for job role: {}", jobRole);

        String prompt = buildPrompt(resumeText, jobRole);
        String responseContent = callOpenAIWithResilience(prompt);

        List<QuestionDTO> questions = parseQuestionsFromResponse(responseContent);
        log.info("Successfully generated {} interview questions for role: {}", questions.size(), jobRole);

        return questions;
    }

    /**
     * Build the prompt to send to OpenAI.
     */
    private String buildPrompt(String resumeText, String jobRole) {
        return String.format(
                """
                        Based on this resume and the job role '%s', generate 10 relevant interview questions. \
                        Include a mix of technical, behavioral, and situational questions. \
                        Format your response as a JSON array with objects containing these fields: \
                        questionText (string), category (one of: TECHNICAL, BEHAVIORAL, SITUATIONAL), \
                        difficulty (one of: EASY, MEDIUM, HARD).

                        Respond with ONLY the JSON array, no other text or markdown formatting.

                        Resume:
                        %s
                        """,
                jobRole, resumeText);
    }

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
     * The circuit breaker tracks the failure rate of OpenAI API calls.
     * If the failure rate exceeds the configured threshold (default 50%
     * over 10 calls), subsequent calls fail fast with
     * {@code CallNotPermittedException} for 60 seconds, preventing
     * unnecessary API calls to a degraded service.
     * </p>
     *
     * @param prompt the prompt to send to OpenAI
     * @return the assistant's response content
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
     * @param prompt the prompt to send to OpenAI
     * @return the assistant's response content (typically a JSON string)
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
                "You are an expert interviewer and career coach. " +
                        "Generate high-quality interview questions tailored to the candidate's resume and target role. "
                        +
                        "Always respond with valid JSON only.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        log.debug("Calling OpenAI Chat Completion: model={}, maxTokens={}", model, maxTokens);
        ResponseEntity<String> response = openAIRestTemplate.postForEntity(
                OPENAI_API_URL, requestBody, String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Empty response from OpenAI API");
        }

        return extractContentFromResponse(response.getBody());
    }

    /**
     * Extract the assistant's message content from the OpenAI response JSON.
     */
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
                log.info("OpenAI token usage — prompt: {}, completion: {}, total: {}",
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

    /**
     * Parse a JSON array string into a list of QuestionDTOs.
     * Handles cases where the response might be wrapped in markdown code fences.
     */
    private List<QuestionDTO> parseQuestionsFromResponse(String content) {
        try {
            // Strip markdown code fences if present
            String jsonContent = content;
            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.replaceAll("^```(?:json)?\\s*", "");
                jsonContent = jsonContent.replaceAll("\\s*```$", "");
            }
            jsonContent = jsonContent.trim();

            List<QuestionDTO> questions = objectMapper.readValue(
                    jsonContent, new TypeReference<>() {
                    });

            // Validate and normalize each question
            List<QuestionDTO> validatedQuestions = new ArrayList<>();
            for (QuestionDTO q : questions) {
                if (q.getQuestionText() != null && !q.getQuestionText().isBlank()) {
                    // Normalize category
                    if (q.getCategory() != null) {
                        q.setCategory(q.getCategory().toUpperCase());
                    } else {
                        q.setCategory("TECHNICAL");
                    }
                    // Normalize difficulty
                    if (q.getDifficulty() != null) {
                        q.setDifficulty(q.getDifficulty().toUpperCase());
                    } else {
                        q.setDifficulty("MEDIUM");
                    }
                    validatedQuestions.add(q);
                }
            }

            if (validatedQuestions.isEmpty()) {
                throw new RuntimeException("No valid questions could be parsed from OpenAI response");
            }

            return validatedQuestions;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse question JSON from OpenAI response: {}", content, e);
            throw new RuntimeException("Failed to parse interview questions from AI response. "
                    + "The response was not in the expected JSON format.", e);
        }
    }

}
