package com.example.lottery.security.controller;

import com.example.lottery.security.dto.AuthResponse;
import com.example.lottery.security.dto.LoginRequest;
import com.example.lottery.security.dto.RefreshRequest;
import com.example.lottery.security.dto.RegisterRequest;
import com.example.lottery.security.entity.Role;
import com.example.lottery.security.entity.User;
import com.example.lottery.security.repository.UserRepository;
import com.example.lottery.security.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Optional<User> user = userRepository.findByUsername(request.username());


        if (user.isEmpty() || !passwordEncoder.matches(request.password(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
        }

        String accessToken = jwtService.generateAccessToken(user.get().getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.get().getUsername());

        Date accessExp = jwtService.getExpiration(accessToken);
        Date refreshExp = jwtService.getExpiration(refreshToken);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.get().getUsername());
        response.setRole(user.get().getRole());
        response.setAccessExpiresAt(accessExp.toInstant());
        response.setRefreshExpiresAt(refreshExp.toInstant());

        return ResponseEntity.ok(response);
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Пользователь уже существует");
        }


        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(request.getRole());

        userRepository.save(newUser);

        return ResponseEntity.ok("Пользователь: " + request.getUsername() + " зарегистрирован");
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
            return ResponseEntity.badRequest().body("Нет токена");
        }

        String token = authHeader.substring(7);
        jwtService.blacklist(token);

        return ResponseEntity.ok("Пользователь вышел");
    }

}