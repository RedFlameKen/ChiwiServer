package com.voxopus.chiwiserver.service.user;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.encryption.Hasher;
import com.voxopus.chiwiserver.model.user.AuthToken;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.user.AuthTokenRepository;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class AuthTokenService {

    private static final int TOKEN_VALIDITY_PEROID = 10;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private UserRepository userRepository;
    
    public Checker<AuthToken> generateAuthToken(Long userId){
        Optional<User> user = userRepository.findById(userId);

        if(!user.isPresent())
            return Checker.fail("user not found");

        SecureRandom rand = new SecureRandom();
        int num = rand.nextInt(256);

        Hasher hasher = new Hasher();

        String key;
        try {
            key = hasher.hash(String.valueOf(num));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return Checker.fail(e, "failed to generate token");
        }

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.DATE, TOKEN_VALIDITY_PEROID);

        AuthToken token = AuthToken.builder()
            .token(key)
            .user(user.get())
            .expiration_date(expirationDate)
            .build();

        authTokenRepository.save(token);

        return Checker.ok("successfully generated token", token);
    }

    public Checker<AuthToken> getAuthToken(Long userId){
        Optional<User> user = userRepository.findById(userId);

        if(!user.isPresent())
            return Checker.fail("user not found");

        Optional<AuthToken> token = authTokenRepository.findByUserId(userId);

        if(!token.isPresent())
            return Checker.fail("user has no token");

        if(isTokenExpired(token.get())){
            deleteAuthToken(token.get());
            return Checker.fail("token has expired");
        }

        return Checker.ok("successfully fetched auth token", token.get());
    }

    public Checker<User> findUserByAuthToken(String authToken){
        if(authToken == null)
            return Checker.fail("not authorized");

        Optional<AuthToken> token = authTokenRepository.findByToken(authToken);

        if(!token.isPresent())
            return Checker.fail("not authorized");

        if(isTokenExpired(token.get())){
            deleteAuthToken(token.get());
            return Checker.fail("token has expired");
        }

        return Checker.ok("successfully found user with auth token",
                token.get().getUser());
    }

    private boolean isTokenExpired(AuthToken token){
        Calendar curDate = Calendar.getInstance();
        return token.getExpiration_date().compareTo(curDate) <= 0;
    }

    public void deleteAuthToken(AuthToken token){
        token.getUser().setAuthToken(null);
        userRepository.save(token.getUser());
        authTokenRepository.delete(token);
    }

}
