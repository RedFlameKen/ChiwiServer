package com.voxopus.chiwiserver.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.user.User;

public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findByNameContainingIgnoreCase(String keyword);

}
