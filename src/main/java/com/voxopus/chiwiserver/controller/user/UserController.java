package com.voxopus.chiwiserver.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.request.user.UserRequestData;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.user.UserService;
import com.voxopus.chiwiserver.util.Checker;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestData body){
        Checker<User> checker = userService.createUser(
                body.getUsername(), 
                body.getPassword(), 
                body.getSalt_iv());

        HttpStatus status;
        ResponseData<?> response;

        if(!checker.isOk()){
            status = HttpStatus.CONFLICT;
            response = ResponseData.builder()
                .statusCode(status.value())
                .message("username is unavailable")
                .build();

            return new ResponseEntity<>(response, status);
        }

        status = HttpStatus.OK;
        response = ResponseData.builder()
            .statusCode(status.value())
            .message("successfully registered the account")
            .build();

        return new ResponseEntity<>(response, status);
    }

}
