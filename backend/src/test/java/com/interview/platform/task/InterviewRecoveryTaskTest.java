package com.interview.platform.task;

import com.interview.platform.model.*;
import com.interview.platform.repository.InterviewRepository;
import com.interview.platform.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterviewRecoveryTask Tests")
class InterviewRecoveryTaskTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private InterviewRecoveryTask recoveryTask;

    @Captor
    private ArgumentCaptor<Interview> interviewCaptor;

    // ── Shared fixtures ─────────────────────────────────────────

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recoveryTask, "videoGenerationTimeoutMinutes", 15);
        ReflectionTestUtils.setField(recoveryTask, "processingTimeoutMinutes", 30);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
    }

    // ── Helper to build a stuck interview ────────────────────────

    private Interview buildInterview(Long id, InterviewStatus status,
                                     LocalDateTime startedAt, LocalDateTime completedAt) {
        Interview interview = new Interview();
        interview.setId(id);
        interview.setUser(testUser);
        interview.setStatus(status);
        interview.setType(InterviewType.VIDEO);
        // Use reflection since startedAt has @PrePersist but we need to set it directly
        ReflectionTestUtils.setField(interview, "startedAt", startedAt);
        if (completedAt != null) {
            interview.setCompletedAt(completedAt);
        }
        return interview;
    }

    // ============================================================
    // recoverStuckInterviews — main sweep
    // ============================================================

    @Nested
    @DisplayName("recoverStuckInterviews — main sweep")
    class MainSweepTests {

        @Test
        @DisplayName("Should recover both GENERATING_VIDEOS and PROCESSING interviews in a single sweep")
        void testRecoverBothTypes() {
            // Arrange
            LocalDateTime longAgo = LocalDateTime.now().minusMinutes(60);
            Interview stuckVideo = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, longAgo, null);
            Interview stuckProcessing = buildInterview(200L, InterviewStatus.PROCESSING, longAgo, longAgo);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckVideo));
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckProcessing));

            when(questionRepository.countByInterviewId(100L)).thenReturn(10L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(Collections.emptyList());

            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — both interviews recovered
            verify(interviewRepository, times(2)).save(interviewCaptor.capture());
            List<Interview> savedInterviews = interviewCaptor.getAllValues();

            assertThat(savedInterviews).hasSize(2);
            assertThat(savedInterviews.get(0).getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
            assertThat(savedInterviews.get(1).getStatus()).isEqualTo(InterviewStatus.FAILED);
        }

        @Test
        @DisplayName("Should do nothing when no stuck interviews exist")
        void testNoStuckInterviews() {
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — no interviews saved
            verify(interviewRepository, never()).save(any(Interview.class));
        }

        @Test
        @DisplayName("Should handle repository query failure gracefully without crashing the sweep")
        void testQueryFailure() {
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenThrow(new RuntimeException("DB connection timeout"));

            // Act & Assert — should propagate since the method doesn't catch DB errors at top level
            assertThatThrownBy(() -> recoveryTask.recoverStuckInterviews())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB connection timeout");
        }
    }

    // ============================================================
    // GENERATING_VIDEOS recovery
    // ============================================================

    @Nested
    @DisplayName("GENERATING_VIDEOS recovery")
    class GeneratingVideosRecoveryTests {

        @Test
        @DisplayName("Should transition stuck GENERATING_VIDEOS interview to IN_PROGRESS")
        void testRecoverVideoGeneration_TransitionsToInProgress() {
            // Arrange — interview stuck for 20 minutes (timeout is 15)
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(20);
            Interview stuckInterview = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckInterview));
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            when(questionRepository.countByInterviewId(100L)).thenReturn(10L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert
            verify(interviewRepository).save(interviewCaptor.capture());
            Interview saved = interviewCaptor.getValue();
            assertThat(saved.getId()).isEqualTo(100L);
            assertThat(saved.getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should recover multiple stuck GENERATING_VIDEOS interviews")
        void testRecoverVideoGeneration_MultipleInterviews() {
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(30);
            Interview stuck1 = buildInterview(101L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);
            Interview stuck2 = buildInterview(102L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);
            Interview stuck3 = buildInterview(103L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuck1, stuck2, stuck3));
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            when(questionRepository.countByInterviewId(anyLong())).thenReturn(10L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(anyLong()))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — all 3 recovered
            verify(interviewRepository, times(3)).save(interviewCaptor.capture());
            interviewCaptor.getAllValues().forEach(interview ->
                    assertThat(interview.getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("Should not recover GENERATING_VIDEOS interview that is within timeout window")
        void testRecoverVideoGeneration_WithinTimeout() {
            // Interview started 5 minutes ago (timeout is 15 — not yet stuck)
            // The repository query uses a cutoff timestamp, so these interviews
            // won't appear in the query results at all.
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — nothing recovered
            verify(interviewRepository, never()).save(any(Interview.class));
        }

        @Test
        @DisplayName("Should count questions with avatars for diagnostic logging")
        void testRecoverVideoGeneration_CountsAvatars() {
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(20);
            Interview stuckInterview = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            // Build questions — 3 with avatars, 2 without
            Question q1 = new Question();
            q1.setId(1L);
            q1.setAvatarVideoUrl("avatar-cache/abc.mp4");

            Question q2 = new Question();
            q2.setId(2L);
            q2.setAvatarVideoUrl("avatar-cache/def.mp4");

            Question q3 = new Question();
            q3.setId(3L);
            q3.setAvatarVideoUrl("avatar-cache/ghi.mp4");

            Question q4 = new Question();
            q4.setId(4L);
            q4.setAvatarVideoUrl(null);

            Question q5 = new Question();
            q5.setId(5L);
            q5.setAvatarVideoUrl("");

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckInterview));
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            when(questionRepository.countByInterviewId(100L)).thenReturn(5L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(List.of(q1, q2, q3, q4, q5));
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — interview still recovered regardless of avatar count
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should continue recovering other interviews when one fails")
        void testRecoverVideoGeneration_PartialFailure() {
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(20);
            Interview stuck1 = buildInterview(101L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);
            Interview stuck2 = buildInterview(102L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuck1, stuck2));
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // First interview's recovery throws, second succeeds
            when(questionRepository.countByInterviewId(101L))
                    .thenThrow(new RuntimeException("DB error on question count"));
            when(questionRepository.countByInterviewId(102L)).thenReturn(5L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(102L))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act — should not throw
            assertThatCode(() -> recoveryTask.recoverStuckInterviews())
                    .doesNotThrowAnyException();

            // Assert — second interview was still recovered
            verify(interviewRepository, times(1)).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getId()).isEqualTo(102L);
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
        }
    }

    // ============================================================
    // recoverVideoGenerationInterview (individual recovery)
    // ============================================================

    @Nested
    @DisplayName("recoverVideoGenerationInterview")
    class RecoverVideoGenerationInterviewTests {

        @Test
        @DisplayName("Should set status to IN_PROGRESS and save")
        void testRecoverSingle_Success() {
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(20);
            Interview interview = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            when(questionRepository.countByInterviewId(100L)).thenReturn(10L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverVideoGenerationInterview(interview);

            // Assert
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should handle interview with null user gracefully")
        void testRecoverSingle_NullUser() {
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(20);
            Interview interview = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);
            interview.setUser(null); // edge case

            when(questionRepository.countByInterviewId(100L)).thenReturn(0L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act — should not throw
            assertThatCode(() -> recoveryTask.recoverVideoGenerationInterview(interview))
                    .doesNotThrowAnyException();

            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should handle interview with zero questions")
        void testRecoverSingle_NoQuestions() {
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(20);
            Interview interview = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            when(questionRepository.countByInterviewId(100L)).thenReturn(0L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverVideoGenerationInterview(interview);

            // Assert — still transitions to IN_PROGRESS
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
        }
    }

    // ============================================================
    // PROCESSING recovery
    // ============================================================

    @Nested
    @DisplayName("PROCESSING recovery")
    class ProcessingRecoveryTests {

        @Test
        @DisplayName("Should transition stuck PROCESSING interview to FAILED using completedAt as reference")
        void testRecoverProcessing_TransitionsToFailed() {
            // completedAt is 40 minutes ago (timeout is 30)
            LocalDateTime startedAt = LocalDateTime.now().minusMinutes(120);
            LocalDateTime completedAt = LocalDateTime.now().minusMinutes(40);
            Interview stuckInterview = buildInterview(200L, InterviewStatus.PROCESSING, startedAt, completedAt);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckInterview));
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getId()).isEqualTo(200L);
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.FAILED);
        }

        @Test
        @DisplayName("Should not recover PROCESSING interview when completedAt is within timeout even if startedAt is old")
        void testRecoverProcessing_CompletedAtWithinTimeout() {
            // startedAt is very old, but completedAt is recent (10 min ago, timeout is 30)
            LocalDateTime startedAt = LocalDateTime.now().minusMinutes(120);
            LocalDateTime completedAt = LocalDateTime.now().minusMinutes(10);
            Interview recentlySubmitted = buildInterview(200L, InterviewStatus.PROCESSING, startedAt, completedAt);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            // The startedAt is old so the query might return it, but the in-memory filter
            // using completedAt should exclude it
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(List.of(recentlySubmitted));
            // The in-memory filter checks completedAt against cutoff

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — NOT recovered because completedAt is within timeout
            verify(interviewRepository, never()).save(any(Interview.class));
        }

        @Test
        @DisplayName("Should use startedAt as fallback when completedAt is null")
        void testRecoverProcessing_NullCompletedAt_FallbackToStartedAt() {
            // completedAt is null (shouldn't happen but defensive), startedAt is 40 min ago
            LocalDateTime startedAt = LocalDateTime.now().minusMinutes(40);
            Interview stuckInterview = buildInterview(200L, InterviewStatus.PROCESSING, startedAt, null);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckInterview));
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — recovered because startedAt (fallback) is beyond timeout
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.FAILED);
        }

        @Test
        @DisplayName("Should recover multiple stuck PROCESSING interviews")
        void testRecoverProcessing_MultipleInterviews() {
            LocalDateTime completedAt = LocalDateTime.now().minusMinutes(60);
            Interview stuck1 = buildInterview(201L, InterviewStatus.PROCESSING,
                    LocalDateTime.now().minusMinutes(120), completedAt);
            Interview stuck2 = buildInterview(202L, InterviewStatus.PROCESSING,
                    LocalDateTime.now().minusMinutes(90), completedAt);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuck1, stuck2));
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — both recovered to FAILED
            verify(interviewRepository, times(2)).save(interviewCaptor.capture());
            interviewCaptor.getAllValues().forEach(interview ->
                    assertThat(interview.getStatus()).isEqualTo(InterviewStatus.FAILED));
        }
    }

    // ============================================================
    // recoverProcessingInterview (individual recovery)
    // ============================================================

    @Nested
    @DisplayName("recoverProcessingInterview")
    class RecoverProcessingInterviewTests {

        @Test
        @DisplayName("Should set status to FAILED and save")
        void testRecoverSingle_Success() {
            LocalDateTime completedAt = LocalDateTime.now().minusMinutes(45);
            Interview interview = buildInterview(200L, InterviewStatus.PROCESSING,
                    LocalDateTime.now().minusMinutes(120), completedAt);

            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverProcessingInterview(interview);

            // Assert
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.FAILED);
        }

        @Test
        @DisplayName("Should handle null completedAt using startedAt for elapsed time calculation")
        void testRecoverSingle_NullCompletedAt() {
            Interview interview = buildInterview(200L, InterviewStatus.PROCESSING,
                    LocalDateTime.now().minusMinutes(60), null);

            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act — should not throw
            assertThatCode(() -> recoveryTask.recoverProcessingInterview(interview))
                    .doesNotThrowAnyException();

            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.FAILED);
        }

        @Test
        @DisplayName("Should handle null user gracefully")
        void testRecoverSingle_NullUser() {
            Interview interview = buildInterview(200L, InterviewStatus.PROCESSING,
                    LocalDateTime.now().minusMinutes(60), LocalDateTime.now().minusMinutes(45));
            interview.setUser(null);

            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act — should not throw
            assertThatCode(() -> recoveryTask.recoverProcessingInterview(interview))
                    .doesNotThrowAnyException();

            verify(interviewRepository).save(any(Interview.class));
        }
    }

    // ============================================================
    // Configuration / timeout edge cases
    // ============================================================

    @Nested
    @DisplayName("Configuration and timeout edge cases")
    class ConfigurationTests {

        @Test
        @DisplayName("Should use configured video generation timeout for cutoff calculation")
        void testCustomVideoTimeout() {
            // Set a very short timeout (1 minute)
            ReflectionTestUtils.setField(recoveryTask, "videoGenerationTimeoutMinutes", 1);

            // Interview started 2 minutes ago — should be considered stuck with 1-min timeout
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(2);
            Interview stuckInterview = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckInterview));
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            when(questionRepository.countByInterviewId(100L)).thenReturn(5L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — recovered
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should use configured processing timeout for cutoff calculation")
        void testCustomProcessingTimeout() {
            // Set a very short timeout (2 minutes)
            ReflectionTestUtils.setField(recoveryTask, "processingTimeoutMinutes", 2);

            LocalDateTime completedAt = LocalDateTime.now().minusMinutes(5);
            Interview stuckInterview = buildInterview(200L, InterviewStatus.PROCESSING,
                    LocalDateTime.now().minusMinutes(30), completedAt);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckInterview));
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — recovered
            verify(interviewRepository).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.FAILED);
        }
    }

    // ============================================================
    // Idempotency
    // ============================================================

    @Nested
    @DisplayName("Idempotency")
    class IdempotencyTests {

        @Test
        @DisplayName("Running recovery twice should not re-process already-recovered interviews")
        void testIdempotency() {
            LocalDateTime stuckSince = LocalDateTime.now().minusMinutes(20);
            Interview stuckInterview = buildInterview(100L, InterviewStatus.GENERATING_VIDEOS, stuckSince, null);

            // First sweep: interview is stuck
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(List.of(stuckInterview))
                    // Second sweep: no more stuck interviews (already recovered)
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            when(questionRepository.countByInterviewId(100L)).thenReturn(5L);
            when(questionRepository.findByInterviewIdOrderByQuestionNumber(100L))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act — run recovery twice
            recoveryTask.recoverStuckInterviews();
            recoveryTask.recoverStuckInterviews();

            // Assert — save called only once (first sweep)
            verify(interviewRepository, times(1)).save(any(Interview.class));
        }
    }

    // ============================================================
    // Mixed scenarios — PROCESSING interviews filtered by completedAt
    // ============================================================

    @Nested
    @DisplayName("PROCESSING in-memory filtering")
    class ProcessingFilteringTests {

        @Test
        @DisplayName("Should filter out PROCESSING interviews where completedAt is recent even if startedAt is old")
        void testMixedProcessingInterviews() {
            LocalDateTime oldStartedAt = LocalDateTime.now().minusMinutes(120);
            LocalDateTime recentCompletedAt = LocalDateTime.now().minusMinutes(5);
            LocalDateTime oldCompletedAt = LocalDateTime.now().minusMinutes(45);

            // Interview 201: started long ago, completed recently — should NOT be recovered
            Interview recent = buildInterview(201L, InterviewStatus.PROCESSING, oldStartedAt, recentCompletedAt);
            // Interview 202: started long ago, completed long ago — SHOULD be recovered
            Interview old = buildInterview(202L, InterviewStatus.PROCESSING, oldStartedAt, oldCompletedAt);

            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.GENERATING_VIDEOS), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(interviewRepository.findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
                    eq(InterviewStatus.PROCESSING), any(LocalDateTime.class)))
                    .thenReturn(List.of(recent, old));
            when(interviewRepository.save(any(Interview.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            recoveryTask.recoverStuckInterviews();

            // Assert — only interview 202 recovered
            verify(interviewRepository, times(1)).save(interviewCaptor.capture());
            assertThat(interviewCaptor.getValue().getId()).isEqualTo(202L);
            assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.FAILED);
        }
    }
}
