package com.example.lottery.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TicketCreateDto {
    @NotNull
    private Long drawId; // ID тиража

    @Size(min = 5, max = 5, message = "Должно быть выбрано 5 чисел")
    @NotNull
    private List<@Min(1) @Max(36) Integer> numbers; // Выбранные числа (например, [1, 5, 10])
    // TODO: Проверить, можно ли значения для валидации подставлять из алгоритма генерации
}
