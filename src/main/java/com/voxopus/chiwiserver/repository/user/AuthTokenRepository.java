package com.voxopus.chiwiserver.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voxopus.chiwiserver.model.user.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long>{

    Optional<AuthToken> findByToken(String token);

    Optional<AuthToken> findByUserId(Long userId);

    Long deleteByToken(String token);
}
