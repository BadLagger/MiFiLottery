package com.example.lottery.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LotteryTypeCreateDto {
  @NotBlank
  @Size(max = 100)
  private String description;

  @DecimalMin("0.01")
  @DecimalMax("10000.00")
  private BigDecimal ticketPrice;

  @PositiveOrZero private Integer minTicket;

  @DecimalMin("0.01")
  @DecimalMax("0.99")
  private Double prizePoolPercentage;

  @NotNull private AlgorithmRules algorithmRules;
}
