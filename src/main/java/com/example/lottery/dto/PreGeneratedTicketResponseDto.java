package com.example.lottery.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreGeneratedTicketResponseDto {
  @JsonIgnore private Long id; // Идентификатор предсозданного билета
  private Long drawId; // ID тиража
  private List<Integer> numbers; // JSON с числами (например, [1, 5, 10])
  private boolean issued = false; // Флаг "выдан"
}