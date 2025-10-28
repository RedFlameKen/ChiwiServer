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

import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.request.reviewer.CreateAnswerRequestData;
import com.voxopus.chiwiserver.request.reviewer.CreateFlashcardRequestData;
import com.voxopus.chiwiserver.request.reviewer.CreateReviewerRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.reviewer.ReviewerService;
import com.voxopus.chiwiserver.service.user.AuthTokenService;
import com.voxopus.chiwiserver.util.Checker;
import com.voxopus.chiwiserver.util.HeaderUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/reviewers")
public class ReviewerController {

    @Autowired
    private ReviewerService reviewerService;

    @Autowired
    private AuthTokenService authTokenService;

    @PostMapping("create")
    public ResponseEntity<?> createReviewer(HttpServletRequest request,
            @RequestBody CreateReviewerRequestData body) {
        String token = 
            HeaderUtil.extractAuthToken(request.getHeader("Authorization"));

        HttpStatus status;
        ResponseData<?> response;

        Checker<User> user = authTokenService.findUserByAuthToken(token);
        if(!user.isOk()){
            response = ResponseData.builder()
                .status_code(HttpStatus.UNAUTHORIZED.value())
                .message(user.getMessage())
                .data(user.get())
                .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Checker<?> checker = reviewerService.addReviewer(user.get().getId(), body);

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
                .status_code(status.value())
                .message(checker.getMessage())
                .data(checker.get())
                .build();

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("list")
    public ResponseEntity<?> listReviewers(HttpServletRequest request) {
        String token = 
            HeaderUtil.extractAuthToken(request.getHeader("Authorization"));

        ResponseData<?> response;
        HttpStatus status;

        Checker<User> user = authTokenService.findUserByAuthToken(token);
        if(!user.isOk()){
            response = ResponseData.builder()
                .status_code(HttpStatus.UNAUTHORIZED.value())
                .message(user.getMessage())
                .data(user.get())
                .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Checker<?> checker = reviewerService.getReviewersByUserId(user.get().getId());

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
                .status_code(status.value())
                .message(checker.getMessage())
                .data(checker.get())
                .build();

        return new ResponseEntity<>(response, status);

    }

    @PostMapping("{reviewer_id}/flashcard/create")
    public ResponseEntity<?> createFlashcard(@PathVariable("reviewer_id") Long reviewerId,
            @RequestBody CreateFlashcardRequestData body) {

        Checker<?> checker = reviewerService.createFlashcard(reviewerId, body);
        ResponseData<?> response;
        HttpStatus status;

        if(!checker.isOk()){
            if(checker.getException() != null)
                status = HttpStatus.BAD_REQUEST;
            else
                status = HttpStatus.NOT_FOUND;
        } else 
            status = HttpStatus.OK;

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("{reviewer_id}/flashcard")
    public ResponseEntity<?> listFlashcards(@PathVariable("reviewer_id") Long reviewerId){
        Checker<?> checker = reviewerService.listFlashcards(reviewerId);
        ResponseData<?> response;
        HttpStatus status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("{reviewer_id}/flashcard/{flashcard_id}/add")
    public ResponseEntity<?> addAnswer(
            @PathVariable("reviewer_id") Long reviewerId,
            @PathVariable("flashcard_id") Long flashcardId,
            @RequestBody CreateAnswerRequestData body
            ){
        Checker<?> checker = reviewerService.addAnswer(flashcardId, body);
        ResponseData<?> response;
        HttpStatus status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("{reviewer_id}/flashcard/{flashcard_id}")
    public ResponseEntity<?> listAnswers(
            @PathVariable("reviewer_id") Long reviewerId,
            @PathVariable("flashcard_id") Long flashcardId){
        Checker<?> checker = reviewerService.listAnswers(flashcardId);
        ResponseData<?> response;
        HttpStatus status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

}
