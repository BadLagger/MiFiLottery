package com.example.lottery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String telegram,
        BigDecimal balance,
        LocalDateTime createdAt
) {}