package com.interview.platform.repository;

import com.interview.platform.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUserId(Long userId);

    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId ORDER BY r.uploadedAt DESC LIMIT 1")
    Optional<Resume> findLatestByUserId(@Param("userId") Long userId);
}
