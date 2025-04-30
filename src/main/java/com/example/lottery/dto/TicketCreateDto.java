package com.example.lottery.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class TicketCreateDto {
  @NotNull private Long drawId; // ID тиража

  @NotNull private List<Integer> numbers; // Выбранные числа (например, [1, 5, 10])
}
