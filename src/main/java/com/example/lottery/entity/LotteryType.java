package com.example.lottery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="lotterytype")
public class LotteryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal ticketPrice;

    @Column
    private Integer minTicket;

    @Column(nullable = false)
    private Double prizePoolPercentage;
}