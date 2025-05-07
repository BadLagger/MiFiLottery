package com.example.lottery.dto;

import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.Payment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseDto {
    private Long invoiceId;
    private BigDecimal amount;
    private LocalDateTime paymentTime;
    private Payment.PaymentStatus status;
}
