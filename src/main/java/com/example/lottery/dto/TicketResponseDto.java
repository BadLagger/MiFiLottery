package com.example.lottery.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketResponseDto {
    @JsonIgnore
    private Long id;
    private Long drawId;
    private List<Integer> numbers;
    private String status;
}
