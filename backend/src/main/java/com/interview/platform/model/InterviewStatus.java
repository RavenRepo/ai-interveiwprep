package com.interview.platform.model;

/**
 * State machine for interview lifecycle.
 *
 * <pre>
 * CREATED ──► GENERATING_VIDEOS ──► IN_PROGRESS ──► PROCESSING ──► COMPLETED
 *                    │                    │               │
 *                    └────────────────────┴───────────────┴──────► FAILED
 * </pre>
 *
 * <h3>State Descriptions:</h3>
 * <ul>
 *   <li><strong>CREATED:</strong> Interview entity and questions are saved to DB.
 *       No async work has started yet.</li>
 *   <li><strong>GENERATING_VIDEOS:</strong> Avatar video generation (ElevenLabs TTS → D-ID)
 *       is running asynchronously for each question. The frontend should poll or listen
 *       via SSE for readiness. A scheduled recovery task transitions interviews stuck in
 *       this state for more than 15 minutes to IN_PROGRESS (text-only fallback).</li>
 *   <li><strong>IN_PROGRESS:</strong> All avatar videos are ready (or timed out with
 *       text-only fallback). The user can now watch questions and record answers.</li>
 *   <li><strong>PROCESSING:</strong> The user has completed the interview and submitted
 *       it for evaluation. AI feedback generation (OpenAI) is running asynchronously.</li>
 *   <li><strong>COMPLETED:</strong> Feedback has been generated and persisted. The
 *       interview is available for review on the dashboard.</li>
 *   <li><strong>FAILED:</strong> An unrecoverable error occurred during any phase
 *       (e.g., OpenAI question generation failed, critical DB error). The user should
 *       be prompted to retry or contact support.</li>
 * </ul>
 *
 * <h3>P0 Note:</h3>
 * <p>The new states (CREATED, GENERATING_VIDEOS, FAILED) are added in P0 for
 * forward compatibility. The existing service logic continues to use IN_PROGRESS,
 * PROCESSING, and COMPLETED. The P1 event-driven architecture refactor will
 * integrate the full state machine into InterviewService and the new
 * AvatarPipelineListener.</p>
 */
public enum InterviewStatus {

    /** Interview entity saved, questions persisted, no async work started. */
    CREATED,

    /** Avatar videos are being generated asynchronously for each question. */
    GENERATING_VIDEOS,

    /** Videos ready (or timed out). User can record answers. */
    IN_PROGRESS,

    /** User completed the interview. Feedback is being generated. */
    PROCESSING,

    /** Feedback generated and available for review. */
    COMPLETED,

    /** Unrecoverable error — user should retry or contact support. */
    FAILED
}
