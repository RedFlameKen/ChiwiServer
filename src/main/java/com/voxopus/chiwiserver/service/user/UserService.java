package com.voxopus.chiwiserver.service.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Checker<User> createUser(String username, String password, String saltIv){
        if(usernameUsed(username)){
            return Checker.fail("username is unnavailable");
        }

        User user = User.builder()
            .username(username)
            .password(password)
            .salt_iv(saltIv)
            .date_created(new Date())
            .build();
        userRepository.save(user);

        return new Checker<>(user);
    }

    private boolean usernameUsed(String username){
        return userRepository.findByUsername(username).isPresent();
    }

}
