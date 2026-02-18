package com.interview.platform.repository;

import com.interview.platform.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByInterviewIdOrderByQuestionNumber(Long interviewId);

    Long countByInterviewId(Long interviewId);
}
