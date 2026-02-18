package com.interview.platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages Server-Sent Event (SSE) connections for real-time interview
 * status updates.
 *
 * <h3>Purpose:</h3>
 * <p>
 * Provides real-time push notifications to the frontend during the
 * avatar video generation phase. Instead of the frontend polling every
 * few seconds, the server pushes events as each avatar video becomes
 * ready. The frontend has a polling fallback via {@code useInterviewPolling}
 * for environments where SSE connections are dropped.
 * </p>
 *
 * <h3>Event Types:</h3>
 * <ul>
 * <li>{@code avatar-ready} — an avatar video has been generated for
 * a question ({@code questionId}, {@code videoUrl})</li>
 * <li>{@code avatar-failed} — avatar generation failed for a question;
 * frontend should show text-only fallback</li>
 * <li>{@code interview-ready} — all questions processed; interview
 * transitioned to {@code IN_PROGRESS}</li>
 * </ul>
 *
 * <h3>Thread Safety:</h3>
 * <p>
 * Uses {@link ConcurrentHashMap} with {@link CopyOnWriteArrayList}
 * to safely handle concurrent registrations and event sends from
 * different threads (controller thread for registration, virtual
 * thread from {@code AvatarPipelineListener} for events).
 * </p>
 *
 * <h3>Lifecycle:</h3>
 * <p>
 * Emitters are automatically removed on completion, timeout, or error
 * via registered callbacks. The default timeout is 10 minutes (matching
 * the maximum expected avatar pipeline duration).
 * </p>
 *
 * @see com.interview.platform.event.AvatarPipelineListener
 * @see com.interview.platform.controller.InterviewController
 */
@Service
public class SseEmitterService {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterService.class);

    /** Default SSE connection timeout: 10 minutes. */
    private static final long SSE_TIMEOUT_MS = 600_000L;

    /**
     * Map of interviewId → list of active SSE emitters.
     * Multiple browser tabs/sessions can subscribe to the same interview.
     */
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * Register a new SSE emitter for a given interview.
     *
     * <p>
     * The emitter is automatically removed from the map when the
     * connection closes (completion, timeout, or error).
     * </p>
     *
     * @param interviewId the interview to subscribe to
     * @return the configured SseEmitter ready to be returned from a controller
     */
    public SseEmitter register(Long interviewId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        CopyOnWriteArrayList<SseEmitter> list = emitters.computeIfAbsent(
                interviewId, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        log.debug("SSE emitter registered: interviewId={}, activeConnections={}",
                interviewId, list.size());

        // Cleanup callbacks
        Runnable onDone = () -> {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(interviewId);
            }
            log.debug("SSE emitter removed: interviewId={}, remaining={}",
                    interviewId, list.size());
        };

        emitter.onCompletion(onDone);
        emitter.onTimeout(onDone);
        emitter.onError(t -> {
            log.debug("SSE emitter error: interviewId={}, error={}",
                    interviewId, t.getMessage());
            onDone.run();
        });

        return emitter;
    }

    /**
     * Send an event to all SSE subscribers for a given interview.
     *
     * <p>
     * Failed sends (e.g., client disconnected) are silently caught
     * and the emitter is completed/removed. This ensures one broken
     * connection doesn't prevent other subscribers from receiving events.
     * </p>
     *
     * @param interviewId the interview whose subscribers should receive the event
     * @param eventName   the SSE event name (e.g., "avatar-ready",
     *                    "interview-ready")
     * @param data        the event payload (will be serialized to JSON by Spring)
     */
    public void send(Long interviewId, String eventName, Object data) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.get(interviewId);
        if (list == null || list.isEmpty()) {
            log.debug("No SSE subscribers for interviewId={}, event '{}' not sent",
                    interviewId, eventName);
            return;
        }

        log.debug("Sending SSE event '{}' to {} subscribers for interviewId={}",
                eventName, list.size(), interviewId);

        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException | IllegalStateException e) {
                log.debug("Failed to send SSE event to client: {}", e.getMessage());
                emitter.completeWithError(e);
            }
        }
    }

    /**
     * Complete all SSE connections for a given interview.
     *
     * <p>
     * Called when the interview transitions to {@code IN_PROGRESS},
     * signaling that all avatar videos have been processed and the
     * frontend can proceed. The cleanup callbacks handle removal from
     * the map.
     * </p>
     *
     * @param interviewId the interview whose connections should be closed
     */
    public void completeAll(Long interviewId) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.get(interviewId);
        if (list == null || list.isEmpty()) {
            return;
        }

        log.debug("Completing all SSE connections for interviewId={}", interviewId);

        for (SseEmitter emitter : list) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("Error completing SSE emitter: {}", e.getMessage());
            }
        }
    }
}
