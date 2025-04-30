package com.example.lottery.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDto {
    private UUID invoiceId;
    private BigDecimal amount;
    private String cardNumber;
    private String cvc;
    private DrawStatus drawStatus;
}