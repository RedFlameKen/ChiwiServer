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

import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.request.reviewer.ReviewerSetupRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.reviewer.ReviewerService;
import com.voxopus.chiwiserver.service.reviewer.ReviewerSetupSessionService;
import com.voxopus.chiwiserver.service.user.AuthTokenService;
import com.voxopus.chiwiserver.util.Checker;
import com.voxopus.chiwiserver.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/reviewer/setup")
public class ReviewerSetupController {

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private ReviewerService reviewerService;

    @Autowired
    private ReviewerSetupSessionService reviewerSetupSessionService;

    @PostMapping("start")
    public ResponseEntity<?> startMapping(@RequestBody ReviewerSetupRequestData data, HttpServletRequest request){
        ResponseData<?> response;
        HttpStatus status;

        Cookie tokenCookie = CookieUtil.getCookie(request.getCookies(), "auth_token");
        Cookie usernameCookie = CookieUtil.getCookie(request.getCookies(), "username");
        if(tokenCookie == null || usernameCookie == null){
            status = HttpStatus.BAD_REQUEST;
            response = ResponseData.builder()
                .status_code(status.value())
                .message("missing auth token")
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        }

        String authToken = tokenCookie.getValue();
        String username = usernameCookie.getValue();
        Checker<User> user = authTokenService.checkUserToken(username, authToken);
        if(!user.isOk()){
            status = HttpStatus.UNAUTHORIZED;
            response = createResponseData(status, user);
            return new ResponseEntity<>(response, status);
        }

        Checker<?> checker = reviewerSetupSessionService
            .startSession(user.get(), data.getReviewer_id());

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

    @PostMapping("command")
    public ResponseEntity<?> commandMapping(@RequestPart(name = "audio", required=true) MultipartFile file, HttpServletRequest request) throws IOException{
        ResponseData<?> response;
        HttpStatus status;

        Cookie tokenCookie = CookieUtil.getCookie(request.getCookies(), "auth_token");
        Cookie usernameCookie = CookieUtil.getCookie(request.getCookies(), "username");
        if(tokenCookie == null || usernameCookie == null){
            status = HttpStatus.BAD_REQUEST;
            response = ResponseData.builder()
                .status_code(status.value())
                .message("missing auth token")
                .data(null)
                .build();
            return new ResponseEntity<>(response, status);
        }

        String authToken = tokenCookie.getValue();
        String username = usernameCookie.getValue();
        Checker<User> user = authTokenService.checkUserToken(username, authToken);
        if(!user.isOk()){
            status = HttpStatus.UNAUTHORIZED;
            response = createResponseData(status, user);
            return new ResponseEntity<>(response, status);
        }

        Checker<?> transcription = reviewerSetupSessionService.processCommand(user.get().getId(), file.getBytes());

        status = transcription.isOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        response = createResponseData(status, transcription);

        return new ResponseEntity<>(response, status);
    }
    
    public ResponseData<?> createResponseData(HttpStatus status, Checker<?> checker){
        return ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();
    }

}
