package com.example.lottery.dto;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.AlgorithmType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LotteryTypeCreateDto {

  @NotBlank(message = "Описание обязательно")
  private String description;

  @NotNull(message = "Цена билета обязательна")
  @Min(value = 1, message = "Цена должна быть больше 0")
  private BigDecimal ticketPrice;

  @NotNull(message = "Минимальное количество билетов обязательно")
  @Min(value = 1, message = "Минимум 1 билет")
  private Integer minTicket;

  @NotNull(message = "Процент призового фонда обязателен")
  private Double prizePoolPercentage;

  @NotNull(message = "Тип алгоритма обязателен")
  private AlgorithmType algorithmType;

  @NotNull(message = "Правила алгоритма обязательны")
  private AlgorithmRules algorithmRules;
}
