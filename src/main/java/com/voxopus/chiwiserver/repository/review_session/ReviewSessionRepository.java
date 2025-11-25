package com.voxopus.chiwiserver.repository.review_session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.review_session.ReviewSession;

public interface ReviewSessionRepository extends JpaRepository<ReviewSession, Long>{

    public Optional<ReviewSession> findByUserId(Long id);
    public Optional<ReviewSession> findByReviewerId(Long id);
    
}
