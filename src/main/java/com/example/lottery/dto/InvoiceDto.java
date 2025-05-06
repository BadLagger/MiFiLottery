package com.example.lottery.dto;

import com.example.lottery.entity.Invoice;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
  private Long id;
  private Long userId;
  private String ticketData;
  private LocalDateTime registerTime;
  private String paymentLink;
  private Invoice.Status status;
  private int cancelled;
}
