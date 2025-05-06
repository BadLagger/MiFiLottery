package com.example.lottery.security.controller;

import com.example.lottery.entity.User;
import com.example.lottery.mapper.UserMapper;
import com.example.lottery.repository.UserRepository;
import com.example.lottery.security.dto.AuthResponse;
import com.example.lottery.security.dto.LoginRequest;
import com.example.lottery.security.dto.RefreshRequest;
import com.example.lottery.security.dto.RegisterRequest;
import com.example.lottery.security.service.JwtService;
import com.example.lottery.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    //private final UserRepository userRepository;
    private final UserService userService;
    //private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws AccessDeniedException {

        User user = userService.login(request);

        String accessToken = jwtService.generateAccessToken(user.getName());
        String refreshToken = jwtService.generateRefreshToken(user.getName());

        Date accessExp = jwtService.getExpiration(accessToken);
        Date refreshExp = jwtService.getExpiration(refreshToken);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getName());
        response.setRole(user.getRole());
        response.setAccessExpiresAt(accessExp.toInstant());
        response.setRefreshExpiresAt(refreshExp.toInstant());

        return ResponseEntity.ok(response);
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        log.info("Try to register: {}, {}", request.getName(), request.getRole());

        userService.registerUser(request);

        return ResponseEntity.ok("User:  " + request.getName() + " registration success");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();

        String username = jwtService.extractUsername(refreshToken);

        if (!jwtService.isTokenValid(refreshToken, username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Недействительный refresh токен");
        }

        String newAccessToken = jwtService.generateAccessToken(username);
        Instant newAccessExp = jwtService.getExpiration(newAccessToken).toInstant();

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("accessExpiresAt", newAccessExp);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No token");
        }

        String token = authHeader.substring(7);
        jwtService.blacklist(token);

        return ResponseEntity.ok("User exit");
    }

}