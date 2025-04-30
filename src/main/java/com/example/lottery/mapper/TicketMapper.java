package com.example.lottery.mapper;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Ticket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class TicketMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "data", expression = "java(mapNumbersToJson(dto.getNumbers()))")
    public abstract Ticket toEntity(TicketCreateDto dto);

    @Mapping(target = "numbers", expression = "java(mapJsonToNumbers(ticket.getData()))")
    @Mapping(target = "drawId", source = "draw.id")
    @Mapping(target = "status", source = "status")
    public abstract TicketResponseDto toDto(Ticket ticket);

    protected String mapNumbersToJson(List<Integer> numbers) {
        try {
            return objectMapper.writeValueAsString(Map.of("numbers", numbers));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации чисел: " + numbers, e);
        }
    }

    protected List<Integer> mapJsonToNumbers(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, List<Integer>>>() {})
                    .get("numbers");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка парсинга чисел: " + json, e);
        }
    }
}