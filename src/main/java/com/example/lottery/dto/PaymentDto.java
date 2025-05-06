package com.example.lottery.dto;

import com.example.lottery.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
  private Long id;
  private Long invoiceId;
  private BigDecimal amount;
  private LocalDateTime paymentTime;
  private Payment.Status status;
}
