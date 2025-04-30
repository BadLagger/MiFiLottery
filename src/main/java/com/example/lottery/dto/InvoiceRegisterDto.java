package com.example.lottery.dto;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceRegisterDto {
    private Long userId;
    private List<String> ticketData;
}