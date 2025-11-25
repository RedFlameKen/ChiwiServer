package com.voxopus.chiwiserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.response.ResponseData;
import com.voxopus.chiwiserver.service.user.AuthTokenService;
import com.voxopus.chiwiserver.util.Checker;
import com.voxopus.chiwiserver.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;

public abstract class RestControllerWithCookies {

    @Autowired
    protected AuthTokenService authTokenService;

    protected CookieResult<UsernameAndTokenCookie> getUsernameAndTokenCookie(HttpServletRequest request){
        Cookie tokenCookie = CookieUtil.getCookie(request.getCookies(), "auth_token");
        Cookie usernameCookie = CookieUtil.getCookie(request.getCookies(), "username");

        String authToken = tokenCookie.getValue();
        String username = usernameCookie.getValue();
        Checker<User> user = authTokenService.checkUserToken(username, authToken);
        if(!user.isOk()){
            final var status = HttpStatus.UNAUTHORIZED;
            final var response = createResponseData(status, user);
            return CookieResult.fail(new ResponseEntity<>(response, status));
        }

        return CookieResult.ok(UsernameAndTokenCookie.builder()
                .username(username)
                .user(user.get())
                .auth_token(authToken)
                .build());
    }

    public ResponseData<?> createResponseData(HttpStatus status, Checker<?> checker){
        return ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();
    }

    @Data
    @Builder
    protected static class UsernameAndTokenCookie {
        User user;
        String username;
        String auth_token;
    }

    @Data
    @Builder
    protected static class CookieResult<C>{
        C cookie;
        ResponseEntity<?> responseEntity;

        public boolean isOk(){
            return responseEntity == null;
        }

        public static <C> CookieResult<C> ok(C cookie){
            return CookieResult.<C>builder()
                .cookie(cookie)
                .build();
        }

        public static <C> CookieResult<C> fail(ResponseEntity<?> responseEntity){
            return CookieResult.<C>builder()
                .responseEntity(responseEntity)
                .build();
        }

    }
    
}
