package com.voxopus.chiwiserver.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.user.User;

public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findByUsernameContainingIgnoreCase(String keyword);

    Optional<User> findByUsername(String username);

}
