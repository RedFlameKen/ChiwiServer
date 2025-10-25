package com.voxopus.chiwiserver.service.reviewer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.model.reviewer.ReviewSession;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.reviewer.ReviewSessionRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class ReviewerService {

    @Autowired
    private ReviewSessionRepository reviewSessionRepository;

    @Autowired
    private ReviewerRepository reviewerRepository;

    @Autowired
    private UserRepository userRepository;


    public Reviewer addReviewer(String name){
        Reviewer reviewer = Reviewer.builder().name(name).build();
        reviewerRepository.save(reviewer);
        return reviewer;
    }

    public List<Reviewer> getReviewersByUserId(Long userId){
        return reviewerRepository.findByUserId(userId);
    }

    public Checker<ReviewSession> startSession(Long userId, Long reviewerId){
        Optional<User> user = userRepository.findById(userId);

        if(!user.isPresent()){
            return Checker.fail("user not found");
        }

        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);
        if(!reviewer.isPresent()){
            return Checker.fail("reviewer not found");
        }

        List<ReviewSession> sessions
            = reviewSessionRepository.findAllByUserId(userId);
        if(!sessions.isEmpty()){
            return Checker.fail("a session already exists");
        }

        ReviewSession session = ReviewSession.builder()
            .reviewer(reviewer.get())
            .user(user.get())
            .timeStarted(new Date())
            .build();

        return new Checker<>(session);
    }

}
