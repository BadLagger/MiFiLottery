package com.example.lottery.dto;

import jakarta.validation.constraints.*;

public record UserLoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}