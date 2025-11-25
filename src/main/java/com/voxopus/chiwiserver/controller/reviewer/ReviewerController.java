package com.voxopus.chiwiserver.controller.reviewer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voxopus.chiwiserver.controller.RestControllerWithCookies;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.request.reviewer.CreateAnswerRequestData;
import com.voxopus.chiwiserver.request.reviewer.CreateFlashcardRequestData;
import com.voxopus.chiwiserver.request.reviewer.ReviewerRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.reviewer.ReviewerService;
import com.voxopus.chiwiserver.util.Checker;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/reviewer")
public class ReviewerController extends RestControllerWithCookies {

    @Autowired
    private ReviewerService reviewerService;

    @PostMapping("create")
    public ResponseEntity<?> createReviewer(@RequestBody ReviewerRequestData body,
            HttpServletRequest request) {
        HttpStatus status;
        ResponseData<?> response;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<?> checker = reviewerService.addReviewer(cookie.getCookie().getUser().getId(), body);

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
                .status_code(status.value())
                .message(checker.getMessage())
                .data(checker.get())
                .build();

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("list")
    public ResponseEntity<?> listReviewers(@RequestParam("query") Optional<String> query, HttpServletRequest request) {
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<?> checker = reviewerService.getReviewersByUserId(cookie.getCookie().getUser().getId(), query.orElse(null));

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
                .status_code(status.value())
                .message(checker.getMessage())
                .data(checker.get())
                .build();

        return new ResponseEntity<>(response, status);

    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteReviewer(@PathVariable("id") Long reviewerId, HttpServletRequest request) {
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<Reviewer> reviewer = reviewerService.getReviewer(reviewerId);
        if(!reviewer.isOk()){
            status = HttpStatus.NOT_FOUND;
            response = createResponseData(status, reviewer);
            return new ResponseEntity<>(response, status);
        }

        if(cookie.getCookie().getUser().getId() != reviewer.get().getUser().getId()){
            status = HttpStatus.UNAUTHORIZED;
            response = createResponseData(status, Checker.fail("unauthorized access"));
            return new ResponseEntity<>(response, status);
        }

        Checker<?> checker = reviewerService.deleteReviewer(reviewerId);
        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
                .status_code(status.value())
                .message(checker.getMessage())
                .data(checker.get())
                .build();

        return new ResponseEntity<>(response, status);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateReviewer(@PathVariable("id") Long reviewerId, @RequestBody ReviewerRequestData body,
            HttpServletRequest request) {
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<Reviewer> reviewer = reviewerService.getReviewer(reviewerId);
        if(!reviewer.isOk()){
            status = HttpStatus.NOT_FOUND;
            response = createResponseData(status, reviewer);
            return new ResponseEntity<>(response, status);
        }

        if(cookie.getCookie().getUser().getId() != reviewer.get().getUser().getId()){
            status = HttpStatus.UNAUTHORIZED;
            response = createResponseData(status, Checker.fail("unauthorized access"));
            return new ResponseEntity<>(response, status);
        }

        Checker<?> checker = reviewerService.updateReviewer(reviewerId, body);
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
            @RequestBody CreateFlashcardRequestData body,
            HttpServletRequest request) {
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<Reviewer> reviewer = reviewerService.getReviewer(reviewerId);
        if(!reviewer.isOk()){
            status = HttpStatus.NOT_FOUND;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        } else if (reviewer.get().getUser().getId() != cookie.getCookie().getUser().getId()) {
            status = HttpStatus.UNAUTHORIZED;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        }

        Checker<?> checker = reviewerService.createFlashcard(reviewer.get(), body);

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
    public ResponseEntity<?> listFlashcards(@PathVariable("reviewer_id") Long reviewerId, HttpServletRequest request){
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<Reviewer> reviewer = reviewerService.getReviewer(reviewerId);
        if(!reviewer.isOk()){
            status = HttpStatus.NOT_FOUND;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        } else if (reviewer.get().getUser().getId() != cookie.getCookie().getUser().getId()) {
            status = HttpStatus.UNAUTHORIZED;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        }

        Checker<?> checker = reviewerService.listFlashcards(reviewerId);
        status = HttpStatus.OK;

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
            @RequestBody CreateAnswerRequestData body,
            HttpServletRequest request
            ){

        ResponseData<?> response;
        HttpStatus status;
        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<Reviewer> reviewer = reviewerService.getReviewer(reviewerId);
        if(!reviewer.isOk()){
            status = HttpStatus.NOT_FOUND;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        } else if (reviewer.get().getUser().getId() != cookie.getCookie().getUser().getId()) {
            status = HttpStatus.UNAUTHORIZED;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        }

        Checker<?> checker = reviewerService.addAnswer(flashcardId, body);
        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

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
            @PathVariable("flashcard_id") Long flashcardId,
            HttpServletRequest request
            ){
        ResponseData<?> response;
        HttpStatus status;
        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<Reviewer> reviewer = reviewerService.getReviewer(reviewerId);
        if(!reviewer.isOk()){
            status = HttpStatus.NOT_FOUND;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        } else if (reviewer.get().getUser().getId() != cookie.getCookie().getUser().getId()) {
            status = HttpStatus.UNAUTHORIZED;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(reviewer.getMessage())
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        }

        Checker<?> checker = reviewerService.listAnswers(flashcardId);
        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

}
