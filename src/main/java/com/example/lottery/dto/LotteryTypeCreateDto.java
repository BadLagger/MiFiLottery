package com.example.lottery.dto;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;

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
