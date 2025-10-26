package com.voxopus.chiwiserver.controller.reviewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voxopus.chiwiserver.request.reviewer.CreateReviewerRequestData;
import com.voxopus.chiwiserver.service.reviewer.ReviewerService;
import com.voxopus.chiwiserver.util.Checker;

@RestController
@RequestMapping("/review")
public class ReviewerController {

    @Autowired
    private ReviewerService reviewService;

    @PostMapping("create")
    public ResponseEntity<?> createReviewer(@RequestBody CreateReviewerRequestData body){
        Checker<?> checker = reviewService.addReviewer(body);

        if(!checker.isOk()){
            return new ResponseEntity<>(checker.get(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(checker.get(), HttpStatus.OK);
    }

    @GetMapping("test")
    public ResponseEntity<String> testMapping(){
        return ResponseEntity.ok("OK cool");
    }
    
}
