package com.interview.platform.event;

import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.List;

/**
 * Spring Application Event published after interview questions have been
 * persisted to the database and the transaction has committed.
 *
 * <h3>Event-Driven Avatar Pipeline (P1):</h3>
 * <p>This event decouples question creation from avatar video generation.
 * Previously, {@code InterviewService.startInterview()} directly called
 * {@code AvatarVideoService.generateAvatarVideoAsync()} for each question
 * inside the same transaction. This had several problems:</p>
 * <ul>
 *   <li>If the transaction rolled back, async tasks were already dispatched
 *       and would fail with "entity not found" errors.</li>
 *   <li>The caller was tightly coupled to the avatar generation implementation.</li>
 *   <li>No clean recovery path for failed avatar generation — the fire-and-forget
 *       {@code CompletableFuture} callbacks could silently swallow errors.</li>
 * </ul>
 *
 * <h3>New Flow:</h3>
 * <ol>
 *   <li>{@code InterviewService.startInterview()} saves the interview (status
 *       {@code GENERATING_VIDEOS}), saves all questions, then publishes this event.</li>
 *   <li>The event is held by Spring until the transaction commits
 *       ({@code @TransactionalEventListener(phase = AFTER_COMMIT)}).</li>
 *   <li>{@code AvatarPipelineListener.onQuestionsCreated()} receives the event
 *       on an async virtual thread and orchestrates avatar generation for each
 *       question via {@code CachedAvatarService}.</li>
 *   <li>When all questions have avatar videos (or generation fails with fallback),
 *       the listener transitions the interview to {@code IN_PROGRESS}.</li>
 *   <li>If the listener itself fails (e.g., server restart loses the async thread),
 *       {@code InterviewRecoveryTask} detects interviews stuck in
 *       {@code GENERATING_VIDEOS} and transitions them to {@code IN_PROGRESS}
 *       with text-only fallback after a configurable timeout (default: 15 min).</li>
 * </ol>
 *
 * <h3>Immutability:</h3>
 * <p>This event is immutable after construction. The question ID list is wrapped
 * in {@link Collections#unmodifiableList(List)} to prevent accidental mutation
 * by listeners.</p>
 *
 * @see com.interview.platform.event.AvatarPipelineListener
 * @see com.interview.platform.task.InterviewRecoveryTask
 * @see com.interview.platform.service.CachedAvatarService
 */
public class QuestionsCreatedEvent extends ApplicationEvent {

    private final Long interviewId;
    private final List<Long> questionIds;

    /**
     * Create a new QuestionsCreatedEvent.
     *
     * @param source      the object that published this event (typically {@code InterviewService})
     * @param interviewId the ID of the interview whose questions were created
     * @param questionIds the IDs of the newly persisted questions (in question-number order)
     * @throws IllegalArgumentException if interviewId is null or questionIds is null/empty
     */
    public QuestionsCreatedEvent(Object source, Long interviewId, List<Long> questionIds) {
        super(source);

        if (interviewId == null) {
            throw new IllegalArgumentException("interviewId must not be null");
        }
        if (questionIds == null || questionIds.isEmpty()) {
            throw new IllegalArgumentException("questionIds must not be null or empty");
        }

        this.interviewId = interviewId;
        this.questionIds = Collections.unmodifiableList(questionIds);
    }

    /**
     * @return the ID of the interview whose questions were created
     */
    public Long getInterviewId() {
        return interviewId;
    }

    /**
     * @return an unmodifiable list of question IDs in question-number order
     */
    public List<Long> getQuestionIds() {
        return questionIds;
    }

    @Override
    public String toString() {
        return "QuestionsCreatedEvent{interviewId=" + interviewId
                + ", questionCount=" + questionIds.size()
                + ", questionIds=" + questionIds + '}';
    }
}
