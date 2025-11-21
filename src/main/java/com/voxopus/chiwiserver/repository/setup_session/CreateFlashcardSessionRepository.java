package com.voxopus.chiwiserver.repository.setup_session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.setup_session.CreateFlashcardSession;

public interface CreateFlashcardSessionRepository extends JpaRepository<CreateFlashcardSession, Long>{

    Optional<CreateFlashcardSession> findByReviewerSetupSessionId(Long id);
    
}
