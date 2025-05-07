package com.example.lottery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceCreateDto {
    @NotNull Long userId;
    @NotNull List<Integer> numbers;
    @NotNull Long drawId;

}
