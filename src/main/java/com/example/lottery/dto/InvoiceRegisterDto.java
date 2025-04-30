package com.example.lottery.dto;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceRegisterDto {
    private UUID userId;
    private List<String> ticketData;
}