package com.example.lottery.dto;

import com.example.lottery.entity.Invoice;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceResponseDto {
    private Long userId;
    private String drawId;
    private List<Integer> numbers;
    private LocalDateTime registerTime;
    private String paymentLink; //не используем
    private Invoice.InvoiceStatus status;
    private int cancelled;
}