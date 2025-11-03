package com.voxopus.chiwiserver.service.user;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.encryption.Hasher;
import com.voxopus.chiwiserver.model.user.AuthToken;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.request.user.UserRequestData;
import com.voxopus.chiwiserver.response.user.UserCreatedResponseData;
import com.voxopus.chiwiserver.response.user.UserLoginResponseData;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenService authTokenService;

    public Checker<UserCreatedResponseData> createUser(UserRequestData data){
        String username = data.getUsername();
        String password = data.getPassword();

        if(usernameUsed(username)){
            return Checker.fail("username is unnavailable");
        }

        String hashSalt;
        String hashedPassword;

        try {
            Hasher hasher = new Hasher();
            hashSalt = hasher.getSalt();
            hashedPassword = hasher.hash(password);

        } catch (Exception e) {
            e.printStackTrace();
            return Checker.fail(e, "an error occured");
        }

        Date dateCreated = new Date();
        User user = User.builder()
            .username(username)
            .password(hashedPassword)
            .salt(hashSalt)
            .date_created(dateCreated)
            .build();
        userRepository.save(user);

        return Checker.ok("successfully registered the account",
                UserCreatedResponseData.builder()
                .username(username)
                .date_created(dateCreated)
                .build());
    }

    public Checker<UserLoginResponseData> login(UserRequestData data){
        Optional<User> user = 
            userRepository.findByUsername(data.getUsername());

        if(!user.isPresent()){
            return Checker.fail("user does not exist");
        }

        String hashedPassword;
        try {
            Hasher hasher = new Hasher(user.get().getSalt());
            hashedPassword = hasher.hash(data.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return Checker.fail(e, "an error occured");
        }

        String userPassword = user.get().getPassword();

        if(!hashedPassword.equals(userPassword)){
            return Checker.fail("login failed");
        }

        Checker<AuthToken> tokenCheck = 
            authTokenService.getAuthToken(user.get().getId());
        if(!tokenCheck.isOk()){
            tokenCheck = 
                authTokenService.generateAuthToken(user.get().getId());

            if(!tokenCheck.isOk()){
                if(tokenCheck.getException() != null)
                    return Checker.fail(tokenCheck.getException(), 
                            tokenCheck.getMessage());
                else
                    return Checker.fail(tokenCheck.getMessage());
            }
        }

        return Checker.ok("successfully logged in",
                UserLoginResponseData.builder()
                .user_id(user.get().getId())
                .username(data.getUsername())
                .date_logged_in(new Date())
                .auth_token(tokenCheck.get().getToken())
                .build());
    }

    public void signout(User user){
        if(user.getAuthToken() != null){
            authTokenService.deleteAuthToken(user.getAuthToken());
        }

        userRepository.delete(user);
    }

    public void logout(User user){
        authTokenService.deleteAuthToken(user.getAuthToken());
    }

    private boolean usernameUsed(String username){
        return userRepository.findByUsername(username).isPresent();
    }

}
