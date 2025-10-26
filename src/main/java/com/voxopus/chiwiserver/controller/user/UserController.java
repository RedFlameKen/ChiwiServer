package com.voxopus.chiwiserver.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.voxopus.chiwiserver.request.user.UserRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.response.user.UserCreatedResponseData;
import com.voxopus.chiwiserver.response.user.UserLoginResponseData;
import com.voxopus.chiwiserver.service.user.UserService;
import com.voxopus.chiwiserver.util.Checker;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestData body){
        Checker<UserCreatedResponseData> checker = userService.createUser(body);

        HttpStatus status;
        ResponseData<?> response;

        if(!checker.isOk()){
            if(checker.getException() != null)
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            else
                status = HttpStatus.CONFLICT;
        } else 
            status = HttpStatus.OK;

        response = ResponseData.builder()
            .statusCode(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestData userRequestData){
        HttpStatus status;
        ResponseData<?> response;

        Checker<UserLoginResponseData> checker = userService.login(userRequestData);

        if(!checker.isOk()){
            if(checker.getException() != null)
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            else
                status = HttpStatus.UNAUTHORIZED;
        } else
            status = HttpStatus.OK;

        response = ResponseData.builder()
            .statusCode(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

}
