package com.voxopus.chiwiserver.service.reviewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;

@Service
public class ReviewerService {

    @Autowired
    private ReviewerRepository reviewerRepository;

    public void addReviewer(String name){
        reviewerRepository.save(new Reviewer(name));
    }

}
