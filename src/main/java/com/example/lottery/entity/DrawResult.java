package com.example.lottery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="drawresult")
public class DrawResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="draw_id")
    private Draw draw;

    @Column(columnDefinition = "TEXT")
    private String winningCombination;

    @Column(columnDefinition = "TEXT")
    private String winningTickets;

    @Column(nullable = false)
    private LocalDateTime resultTime;

    @Column(nullable = false)
    private BigDecimal prizePool;
}