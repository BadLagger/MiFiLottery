package com.example.lottery.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDto {
    private Long invoiceId;
    private BigDecimal amount;
    private String cardNumber;
    private String cvc;
    private DrawStatus drawStatus;
}