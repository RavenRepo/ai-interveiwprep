package com.interview.platform.repository;

import com.interview.platform.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByInterviewId(Long interviewId);

    Optional<Response> findByQuestionId(Long questionId);

    List<Response> findByInterviewIdOrderByQuestionId(Long interviewId);
}
