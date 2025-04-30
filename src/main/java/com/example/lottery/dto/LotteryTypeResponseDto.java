package com.example.lottery.dto;

import com.example.lottery.entity.AlgorithmType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LotteryTypeResponseDto {
  private Long id;
  private String description;
  private BigDecimal ticketPrice;
  private Integer minTicket;
  private Double prizePoolPercentage;
  private AlgorithmType algorithmType;
  private AlgorithmRules algorithmRules;
}
