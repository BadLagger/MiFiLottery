package com.example.lottery.dto;

import lombok.Data;

import java.util.List;

@Data
public class TicketResponseDto {
    private Long id;
    private Long drawId;
    private List<Integer> numbers;
    private String status;
}
