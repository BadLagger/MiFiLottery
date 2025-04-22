package com.mifisf.lottery.app.entity;

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
public class LotteryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String description;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal ticketPrice;

    @Column(nullable = true)
    private Integer minTicket;

    @Column(nullable = false)
    private Double pricePoolPercentage;
}
