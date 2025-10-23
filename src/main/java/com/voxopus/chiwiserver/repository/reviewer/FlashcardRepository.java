package com.voxopus.chiwiserver.repository.reviewer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.reviewer.Flashcard;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long>{

    List<Flashcard> findByReviewerId(Long id);
    
}
