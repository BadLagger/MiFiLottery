package com.example.lottery.entity;

import com.example.lottery.dto.DrawResultDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Payment {
    private UUID id;
    private UUID invoiceId;
    private BigDecimal amount;
    private LocalDateTime paymentTime;
    private DrawResultDto.PaymentStatus status;
}