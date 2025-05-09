package com.example.lottery.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@Table(name = "lottery_type")
public class LotteryType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false, precision = 8, scale = 2)
  private BigDecimal ticketPrice;

  @Column(nullable = true)
  private Integer minTicket;

  @Column(nullable = false)
  private Double prizePoolPercentage;

  @Enumerated(EnumType.STRING)
  @Column(name = "algorithm_type", nullable = false)
  private AlgorithmType algorithmType;

  @Column(columnDefinition = "jsonb", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private String algorithmRules;
}
