package com.voxopus.chiwiserver.controller.reviewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voxopus.chiwiserver.request.reviewer.CreateReviewerRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.reviewer.ReviewerService;
import com.voxopus.chiwiserver.util.Checker;

@RestController
@RequestMapping("/review")
public class ReviewerController {

    @Autowired
    private ReviewerService reviewerService;

    @PostMapping("create")
    public ResponseEntity<?> createReviewer(@RequestBody CreateReviewerRequestData body){
        Checker<?> checker = reviewerService.addReviewer(body);
        HttpStatus status;
        ResponseData<?> response;

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("list/{user_id}")
    public ResponseEntity<?> listReviewers(@PathVariable("user_id") Long userId){
        Checker<?> checker =
            reviewerService.getReviewersByUserId(userId);
        ResponseData<?> response;
        HttpStatus status;

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    
}
