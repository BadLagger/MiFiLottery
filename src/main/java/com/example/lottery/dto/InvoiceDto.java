package com.example.lottery.dto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Long id;
    private Long userId;
    private JsonNode ticketData;
    private LocalDateTime registerTime;
    private String paymentLink;
    private InvoiceStatus status;
    private int cancelled;
}