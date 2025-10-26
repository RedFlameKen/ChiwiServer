package com.voxopus.chiwiserver.service.user;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.encryption.Decryptor;
import com.voxopus.chiwiserver.encryption.EncryptionFactory;
import com.voxopus.chiwiserver.encryption.Hasher;
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


    public Checker<UserCreatedResponseData> createUser(UserRequestData data){
        String username = data.getUsername();
        String password = data.getPassword();
        String saltIv = data.getSalt_iv();

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
                .dateCreated(dateCreated)
                .build());
    }

    public Checker<UserLoginResponseData> login(UserRequestData data){
        String username = data.getUsername();
        String password = data.getPassword();
        String saltIv = data.getSalt_iv();

        Optional<User> user = userRepository.findByUsername(username);

        if(!user.isPresent()){
            return Checker.fail("user does not exist");
        }

        String hashedPassword;
        try {
            Decryptor decryptor = EncryptionFactory.INSTANCE.getDecryptor(saltIv);
            String decryptedPassword = decryptor.decrypt(password);

            Hasher hasher = new Hasher();
            hashedPassword = hasher.hash(decryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return Checker.fail(e, "an error occured");
        }

        String userPassword = user.get().getPassword();

        if(!hashedPassword.equals(userPassword)){
            return Checker.fail("login failed");
        }

        return Checker.ok("successfully logged in",
                UserLoginResponseData.builder()
                .username(data.getUsername())
                .dateLoggedIn(new Date())
                .build());
    }

    private boolean usernameUsed(String username){
        return userRepository.findByUsername(username).isPresent();
    }

}
