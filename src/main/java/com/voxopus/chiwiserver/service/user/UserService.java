package com.voxopus.chiwiserver.service.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.encryption.Decryptor;
import com.voxopus.chiwiserver.encryption.EncryptionFactory;
import com.voxopus.chiwiserver.encryption.Hasher;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.response.user.CreateUserResponseData;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Checker<CreateUserResponseData> createUser(String username, String password, String saltIv){
        if(usernameUsed(username)){
            return Checker.fail("username is unnavailable");
        }

        String hashSalt;
        String hashedPassword;

        try {
            Decryptor decryptor = EncryptionFactory.INSTANCE.getDecryptor(saltIv);
            String decryptedPassword = decryptor.decrypt(password);

            Hasher hasher = new Hasher();
            hashSalt = hasher.getSalt();
            hashedPassword = hasher.hash(decryptedPassword);

        } catch (Exception e) {
            e.printStackTrace();
            return Checker.fail(e);
        }

        Date dateCreated = new Date();
        User user = User.builder()
            .username(username)
            .password(hashedPassword)
            .salt(hashSalt)
            .date_created(dateCreated)
            .build();
        userRepository.save(user);

        return new Checker<>(CreateUserResponseData.builder()
                .username(username)
                .dateCreated(dateCreated)
                .build());
    }

    private boolean usernameUsed(String username){
        return userRepository.findByUsername(username).isPresent();
    }

}
