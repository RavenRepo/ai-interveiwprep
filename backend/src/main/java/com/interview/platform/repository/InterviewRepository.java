package com.interview.platform.repository;

import com.interview.platform.model.Interview;
import com.interview.platform.model.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByUserId(Long userId);

    List<Interview> findByUserIdAndStatus(Long userId, InterviewStatus status);

    Optional<Interview> findByIdAndUserId(Long id, Long userId);

    /**
     * Find interviews stuck in a given status since before the cutoff time.
     *
     * <p>Used by the P1 scheduled recovery task to detect interviews that have
     * been in {@code GENERATING_VIDEOS} or {@code PROCESSING} for too long
     * (e.g., due to a server restart losing async event listeners, or a
     * circuit breaker staying open). The recovery task transitions them to
     * {@code IN_PROGRESS} (with text-only fallback) or {@code FAILED}.</p>
     *
     * @param status the status to search for (e.g., GENERATING_VIDEOS)
     * @param before interviews with startedAt before this time are considered stuck
     * @return list of stuck interviews ordered by startedAt ascending
     */
    List<Interview> findByStatusAndStartedAtBeforeOrderByStartedAtAsc(
            InterviewStatus status, LocalDateTime before);
}
