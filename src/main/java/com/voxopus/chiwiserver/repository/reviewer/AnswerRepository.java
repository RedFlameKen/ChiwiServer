package com.voxopus.chiwiserver.repository.reviewer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.reviewer.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long>{

    List<Answer> findByFlashcardId(Long id);
    
}
