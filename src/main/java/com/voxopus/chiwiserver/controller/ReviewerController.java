package com.voxopus.chiwiserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.service.ReviewerService;

@RestController
@RequestMapping("/review")
public class ReviewerController {

    @Autowired
    private ReviewerService reviewService;

    @PostMapping("create")
    public ResponseEntity<String> createReviewer(@RequestBody Reviewer entity){
        reviewService.addReviewer(entity.getName());
        return ResponseEntity.ok(entity.toString());
    }
    
    @GetMapping("test")
    public ResponseEntity<String> testMapping(){
        return ResponseEntity.ok("OK cool");
    }
    
}
