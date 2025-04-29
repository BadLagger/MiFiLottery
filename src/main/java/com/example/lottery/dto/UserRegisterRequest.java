package com.example.lottery.dto;

import jakarta.validation.constraints.*;

public record UserRegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 5, max = 20, message = "Password must be 5-20 characters")
        String password,

        @NotBlank(message = "Telegram is required")
        @Pattern(regexp = "^@[a-zA-Z0-9_]{5,32}$", message = "Telegram must start with @ and be 5-32 characters")
        String telegram
) {}