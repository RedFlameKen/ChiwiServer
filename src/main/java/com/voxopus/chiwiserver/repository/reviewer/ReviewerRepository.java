package com.voxopus.chiwiserver.repository.reviewer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.reviewer.Reviewer;

public interface ReviewerRepository extends JpaRepository<Reviewer, Long>{

    List<Reviewer> findByUserId(Long id);

    List<Reviewer> findByUserIdAndNameContainingIgnoreCase(Long id, String keyword);
    
}
