package com.voxopus.chiwiserver.repository.reviewer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.reviewer.ReviewSession;

public interface ReviewSessionRepository extends JpaRepository<ReviewSession, Long>{

    public List<ReviewSession> findAllByUserId(Long id);
    
}
