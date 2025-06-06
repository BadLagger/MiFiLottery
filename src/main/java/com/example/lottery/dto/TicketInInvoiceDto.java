package com.example.lottery.dto;

import com.example.lottery.entity.Invoice;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketInInvoiceDto {
  private Long invoiceId;
  @JsonIgnore
  private Long userId;
  @JsonIgnore
  private Long drawId;
  private List<Integer> numbers;
  private String registerTime;
  private BigDecimal ticketPrice;
  @JsonIgnore
  private String paymentLink;
}
