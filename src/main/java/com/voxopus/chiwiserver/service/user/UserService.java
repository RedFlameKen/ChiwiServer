package com.voxopus.chiwiserver.service.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.user.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void createUser(String username, String password, String saltIv){
        User user = User.builder()
            .username(username)
            .password(password)
            .salt_iv(saltIv)
            .date_created(new Date())
            .build();
        userRepository.save(user);
    }
    
}
