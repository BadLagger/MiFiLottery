package com.example.lottery.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreGeneratedTicketResponseDto {
  private Long id; // Идентификатор предсозданного билета
  private Long drawId; // ID тиража
  private List<Integer> numbers; // JSON с числами (например, [1, 5, 10])
  private boolean issued = false; // Флаг "выдан"
}