package com.example.lottery.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ с данными билета")
public class TicketResponseDto {
  @Schema(description = "Уникальный ID билета", example = "789")
  @JsonIgnore private Long id;
  @Schema(description = "ID тиража, к которому относится билет", example = "123")
  private Long drawId;
  @Schema(description = "Список случайных или выбранных чисел", example = "[5, 12, 18, 23, 42]")
  private List<Integer> numbers;
  @Schema(description = "Статус билета", example = "INGAME", allowableValues = {"INGAME", "WIN", "LOSE", "CANCELLED"})
  private String status;
}
