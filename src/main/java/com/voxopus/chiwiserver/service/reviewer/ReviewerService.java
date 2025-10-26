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
import com.voxopus.chiwiserver.request.reviewer.CreateReviewerRequestData;
import com.voxopus.chiwiserver.response.reviewer.ReviewerResponseData;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class ReviewerService {

    @Autowired
    private ReviewSessionRepository reviewSessionRepository;

    @Autowired
    private ReviewerRepository reviewerRepository;

    @Autowired
    private UserRepository userRepository;


    public Checker<ReviewerResponseData> addReviewer(CreateReviewerRequestData data){
        Optional<User> user = userRepository.findById(data.getUser_id());

        if(!user.isPresent()){
            return Checker.fail("no user with the given id was found");
        }

        Date cur_date = new Date();
        Reviewer reviewer = Reviewer.builder()
            .name(data.getReviewer_name())
            .user(user.get())
            .date_created(cur_date)
            .date_modified(cur_date)
            .build();

        reviewer = reviewerRepository.save(reviewer);
        return Checker.ok("reviewer successfully created", 
                ReviewerResponseData.builder()
                .reviewer_id(reviewer.getId())
                .reviewer_name(reviewer.getName())
                .user_id(reviewer.getUser().getId())
                .build());
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
