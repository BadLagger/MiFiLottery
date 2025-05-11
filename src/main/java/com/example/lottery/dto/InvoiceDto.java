package com.example.lottery.dto;

import com.example.lottery.entity.Invoice;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {
  private Long id;
  private Long userId;
  private String ticketData;
  private LocalDateTime registerTime;
  private String paymentLink;
  private Invoice.Status status;
  private int cancelled;
}
