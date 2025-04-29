package com.example.lottery.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BalanceUpdateRequest(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {}