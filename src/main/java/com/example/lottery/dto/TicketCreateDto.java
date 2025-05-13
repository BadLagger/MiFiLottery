package com.example.lottery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания черновика билета")
public class TicketCreateDto {
  @Schema(description = "ID тиража, к которому относится билет", example = "123", required = true)
  @NotNull(message = "drawId обязательно")
  private Long drawId; // ID тиража

  @Schema(
      description = "Выбранные пользователем числа для билета",
      example = "[5, 12, 18, 23, 42]")
  private List<Integer> numbers; // Выбранные числа (например, [1, 5, 10])
}
