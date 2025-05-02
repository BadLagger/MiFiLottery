package com.example.lottery.dto;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.AlgorithmType;
import java.math.BigDecimal;
import lombok.Data;

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
