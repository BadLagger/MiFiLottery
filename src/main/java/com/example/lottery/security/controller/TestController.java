package com.example.lottery.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> userAccess() {
        return ResponseEntity.ok("✅ Доступ открыт: USER");
    }

    @PutMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("✅ Доступ открыт: ADMIN");
    }

    @GetMapping("/any")
    public ResponseEntity<String> anyoneAccess() {
        return ResponseEntity.ok("🌍 Доступ открыт для всех авторизованных");
    }
}
