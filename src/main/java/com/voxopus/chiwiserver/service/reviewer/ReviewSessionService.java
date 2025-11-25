package com.voxopus.chiwiserver.service.reviewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.repository.review_session.FlashcardQueueItemRepository;
import com.voxopus.chiwiserver.repository.review_session.ReviewSessionRepository;

@Service
public class ReviewSessionService {

    @Autowired
    ReviewSessionRepository reviewSessionRepository;

    @Autowired
    FlashcardQueueItemRepository flashcardQueueItemRepository;

    void startSession(Reviewer reviewer){
        // TODO: check if a session already exists
    }

    void processCommand(byte[] recordingBytes){
        // TODO: process voice recordings
    }
    
}
