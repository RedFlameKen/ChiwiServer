package com.voxopus.chiwiserver.controller.reviewer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.voxopus.chiwiserver.controller.RestControllerWithCookies;
import com.voxopus.chiwiserver.request.reviewer.CommandRequestData;
import com.voxopus.chiwiserver.request.reviewer.ReviewSessionRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.reviewer.ReviewSessionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/reviewer/review")
public class ReviewSessionController extends RestControllerWithCookies {

    @Autowired
    ReviewSessionService reviewSessionService;

    @PostMapping("/start")
    public ResponseEntity<?> startSession(HttpServletRequest request, @RequestBody ReviewSessionRequestData data){
        HttpStatus status;
        ResponseData<?> response;
        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        final var checker = reviewSessionService.startSession(cookie.getCookie().getUser(), data.getReviewer_id());

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.CONFLICT;
        response = createResponseData(status, checker);

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSession(HttpServletRequest request, @RequestBody ReviewSessionRequestData data){
        HttpStatus status;
        ResponseData<?> response;
        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        final var checker = reviewSessionService.cancelSession(cookie.getCookie().getUser(), data.getReviewer_id());

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        response = createResponseData(status, checker);

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/command/input")
    public ResponseEntity<?> processCommandText(HttpServletRequest request, @RequestBody CommandRequestData data) throws IOException{
        HttpStatus status;
        ResponseData<?> response;
        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        final var checker = reviewSessionService.processCommand(cookie.getCookie().getUser_id(), data.getCommand());

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        response = createResponseData(status, checker);

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/command")
    public ResponseEntity<?> processCommand(
            @RequestPart(name = "audio", required=true) MultipartFile file,
            HttpServletRequest request) throws IOException{
        HttpStatus status;
        ResponseData<?> response;
        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        final var checker = reviewSessionService.processCommand(cookie.getCookie().getUser_id(), file.getBytes());

        status = checker.isOk() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        response = createResponseData(status, checker);

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/finish")
    public ResponseEntity<?> finishSession(HttpServletRequest request, @RequestBody ReviewSessionRequestData body){
        HttpStatus status;
        ResponseData<?> response;
        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        final var checker = reviewSessionService.showSessionResults(cookie.getCookie().getUser());
        status = checker.isOk() ? HttpStatus.OK : HttpStatus.CONFLICT;
        response = createResponseData(status, checker);

        return new ResponseEntity<>(response, status);
    }

}
