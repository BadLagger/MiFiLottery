package com.example.lottery.security.repository;

import com.example.lottery.security.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
    Optional<AuthUser> findByUsername(String username);
}
