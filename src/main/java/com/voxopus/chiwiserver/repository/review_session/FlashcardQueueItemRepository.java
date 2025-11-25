package com.voxopus.chiwiserver.repository.review_session;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.review_session.FlashcardQueueItem;

public interface FlashcardQueueItemRepository extends JpaRepository<FlashcardQueueItem, Long>{

    List<FlashcardQueueItem> findAllByReviewSessionId(Long id);
    
}
