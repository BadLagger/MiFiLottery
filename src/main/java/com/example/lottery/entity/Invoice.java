package com.example.lottery.entity;

import com.example.lottery.dto.InvoiceStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class Invoice {
    private UUID id;
    private UUID userId;
    private List<String> ticketData; //////////// JSON каждого билета как String
    private LocalDateTime registerTime;
    private String paymentLink;
    private InvoiceStatus status;
    private int cancelled; // 0 - не отменен, 1 - отменен
}