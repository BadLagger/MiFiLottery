package com.example.lottery.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = false)
public record DrawRequestDto(
        @NotBlank(message = "Name of Draw is mandatory!")
        String name,
        @NotNull(message = "Lottery Type Id is mandatory!")
        Long lotteryTypeId,
        @FutureOrPresent(message = "Start Time should be in future!")
        LocalDateTime startTime,
        Integer duration
) {}
