package com.example.lottery.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateDto {
  private Long drawId; // ID тиража

  private List<Integer> numbers; // Выбранные числа (например, [1, 5, 10])
}
