package com.voxopus.chiwiserver.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.request.user.UserRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.response.user.UserCreatedResponseData;
import com.voxopus.chiwiserver.response.user.UserLoginResponseData;
import com.voxopus.chiwiserver.service.user.AuthTokenService;
import com.voxopus.chiwiserver.service.user.UserService;
import com.voxopus.chiwiserver.util.Checker;
import com.voxopus.chiwiserver.util.HeaderUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthTokenService authTokenService;

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
            .status_code(status.value())
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
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String token = 
            HeaderUtil.extractAuthToken(request.getHeader("Authorization"));

        ResponseData<?> response;
        HttpStatus status;

        Checker<User> user = authTokenService.findUserByAuthToken(token);

        if(!user.isOk()){
            status = HttpStatus.UNAUTHORIZED;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(user.getMessage())
                .build();
            return new ResponseEntity<>(response, status);
        }

        userService.logout(user.get());

        status = HttpStatus.OK;
        response = ResponseData.builder()
            .status_code(status.value())
            .message("successfully logged out")
            .build();

        return new ResponseEntity<>(response, status);
    }

    @DeleteMapping("/signout")
    public ResponseEntity<?> signout(HttpServletRequest request){
        String token = 
            HeaderUtil.extractAuthToken(request.getHeader("Authorization"));

        ResponseData<?> response;
        HttpStatus status;

        Checker<User> user = authTokenService.findUserByAuthToken(token);

        if(!user.isOk()){
            status = HttpStatus.UNAUTHORIZED;
            response = ResponseData.builder()
                .status_code(status.value())
                .message(user.getMessage())
                .build();
            return new ResponseEntity<>(response, status);
        }

        userService.signout(user.get());
        status = HttpStatus.OK;
        response = ResponseData.builder()
            .status_code(status.value())
            .message("successfully signed user out")
            .build();

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/test")
    public ResponseEntity<?> testMapping(){
        System.out.printf("accessed\n");
        return ResponseEntity.ok("test mapping reached");
    }

    @PostMapping("/test")
    public ResponseEntity<?> testMappingPost(@RequestBody String data){
        System.out.printf("test mapping reached, data: " + data + "\n");
        return ResponseEntity.ok("test mapping reached, data: " + data);
    }

}
