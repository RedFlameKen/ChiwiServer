package com.voxopus.chiwiserver.repository.setup_session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.setup_session.SetupStep;

public interface SetupStepRepository extends JpaRepository<SetupStep, Long>{

    Optional<SetupStep> findByReviewerSetupSessionId(Long id);

    
}
