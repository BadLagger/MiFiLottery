package com.example.lottery.dto;

import java.time.LocalDateTime;

public record DrawResultDto(
        Long id,
        Long drawId,
        String winningCombination,
        LocalDateTime resultTime
) {}
