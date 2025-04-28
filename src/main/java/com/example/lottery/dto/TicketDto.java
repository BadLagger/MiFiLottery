package com.example.lottery.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TicketDto {
  private Long id;
  private Map<String, Object> data;
  private Long userId;
  private Long drawId;
  private TicketStatus status;
}
