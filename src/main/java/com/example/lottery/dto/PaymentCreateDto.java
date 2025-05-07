package com.example.lottery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class PaymentCreateDto {
    @NotNull Long invoiceId;
    @NotNull BigDecimal amount; //получить через ticket?
}