package com.voxopus.chiwiserver.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
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
import com.voxopus.chiwiserver.util.CookieUtil;
import com.voxopus.chiwiserver.util.HeaderUtil;

import jakarta.servlet.http.Cookie;
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

        Map<String, List<String>> headers = null;

        if(!checker.isOk()){
            if(checker.getException() != null)
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            else
                status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.OK;

            ResponseCookie tokenCookie = 
                ResponseCookie.from("auth_token", checker.get().getAuth_token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(7 * 24 * 3600)
                .build();

            ResponseCookie usernameCookie = 
                ResponseCookie.from("username", checker.get().getUsername())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(7 * 24 * 3600)
                .build();

            headers = Map.of(HttpHeaders.SET_COOKIE, 
                    List.of(tokenCookie.toString(),usernameCookie.toString()));
        }

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        if(headers != null){
            return new ResponseEntity<>(response, MultiValueMap.fromMultiValue(headers), status);
        }
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/login/auth")
    public ResponseEntity<?> relogin(HttpServletRequest request){
        HttpStatus status;
        ResponseData<?> response;

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

        String auth_token = tokenCookie.getValue();
        String username = usernameCookie.getValue();

        Checker<UserLoginResponseData> checker = userService.relogin(auth_token, username);

        Map<String, List<String>> headers = null;
        if(!checker.isOk()){
            if(checker.getException() != null)
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            else
                status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.OK;

            ResponseCookie tokenResponseCookie = 
                ResponseCookie.from("auth_token", checker.get().getAuth_token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(7 * 24 * 3600)
                .build();
            ResponseCookie usernameResponseCookie = 
                ResponseCookie.from("username", checker.get().getUsername())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(7 * 24 * 3600)
                .build();

            headers = Map.of(HttpHeaders.SET_COOKIE, 
                    List.of(tokenResponseCookie.toString(),usernameResponseCookie.toString()));
        }

        response = ResponseData.builder()
            .status_code(status.value())
            .message(checker.getMessage())
            .data(checker.get())
            .build();

        if(headers != null){
            return new ResponseEntity<>(response, MultiValueMap.fromMultiValue(headers), status);
        }
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

    @GetMapping("test")
    public String foo(){
        return "shit";
    }

}
