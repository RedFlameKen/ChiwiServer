package com.voxopus.chiwiserver.repository.setup_session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.setup_session.ReviewerSetupSession;

public interface ReviewerSetupSessionRepository extends JpaRepository<ReviewerSetupSession, Long>{

    Optional<ReviewerSetupSession> findByReviewerId(Long reviewerId);

    Optional<ReviewerSetupSession> findByUserId(Long userId);
    
}
