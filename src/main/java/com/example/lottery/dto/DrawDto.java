package com.example.lottery.dto;

import java.time.LocalDateTime;

public record DrawDto(
        Long id,
        String lotteryType,
        LocalDateTime startTime,
        DrawStatus status
) {}
