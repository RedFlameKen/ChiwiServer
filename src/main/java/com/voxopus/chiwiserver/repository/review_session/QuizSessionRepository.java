package com.voxopus.chiwiserver.repository.review_session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.review_session.QuizSession;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long>{

    Optional<QuizSession> findByReviewSessionId(Long id);
    
}
