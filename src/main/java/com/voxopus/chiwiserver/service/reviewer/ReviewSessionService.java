package com.voxopus.chiwiserver.service.reviewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.repository.review_session.FlashcardQueueItemRepository;
import com.voxopus.chiwiserver.repository.review_session.ReviewSessionRepository;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class ReviewSessionService {

    @Autowired
    ReviewSessionRepository reviewSessionRepository;

    @Autowired
    FlashcardQueueItemRepository flashcardQueueItemRepository;

    public Checker<?> startSession(Long reviewerId){
        // TODO: check if a session already exists
        return Checker.ok("session started", null);
    }

    public Checker<?> processCommand(byte[] recordingBytes){
        // TODO: process voice recordings
        return Checker.ok("command processed!", null);
    }
    
}
