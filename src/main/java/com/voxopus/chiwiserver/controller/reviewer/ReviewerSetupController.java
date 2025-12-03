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
import com.voxopus.chiwiserver.service.reviewer.ReviewerSetupSessionService;
import com.voxopus.chiwiserver.util.Checker;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/reviewer/setup")
public class ReviewerSetupController extends RestControllerWithCookies {

    @Autowired
    private ReviewerSetupSessionService reviewerSetupSessionService;

    @PostMapping("start")
    public ResponseEntity<?> startMapping(@RequestBody ReviewSessionRequestData data, HttpServletRequest request){
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<?> checker = reviewerSetupSessionService
            .startSession(cookie.getCookie().getUser(),
                    data.getReviewer_id());

        if(!checker.isOk())
            if(checker.getException() != null)
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            else
                status = HttpStatus.FORBIDDEN;
        else
            status = HttpStatus.OK;
        response = createResponseData(status, checker);

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("command/input")
    public ResponseEntity<?> commandMapping(HttpServletRequest request, @RequestBody CommandRequestData data) throws IOException{
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<?> transcription = reviewerSetupSessionService.processCommand(cookie.getCookie().getUser().getId(),
                data.getCommand());

        status = transcription.isOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        response = createResponseData(status, transcription);

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("command")
    public ResponseEntity<?> commandMapping(
            @RequestPart(name = "audio", required=true) MultipartFile file,
            HttpServletRequest request) throws IOException{
        ResponseData<?> response;
        HttpStatus status;

        final var cookie = getUsernameAndTokenCookie(request);
        if(!cookie.isOk()){
            return cookie.getResponseEntity();
        }

        Checker<?> transcription = reviewerSetupSessionService.processCommand(cookie.getCookie().getUser().getId(),
                file.getBytes());

        status = transcription.isOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        response = createResponseData(status, transcription);

        return new ResponseEntity<>(response, status);
    }
    
}
