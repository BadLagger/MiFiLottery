package com.example.lottery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Draw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lotteryType;
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PLANNED, ACTIVE, COMPLETED, CANCELLED
    }
}
