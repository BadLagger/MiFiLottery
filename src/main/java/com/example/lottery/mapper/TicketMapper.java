package com.example.lottery.mapper;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Ticket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {
  ObjectMapper mapper = new ObjectMapper();

  @Mapping(target = "data", expression = "java(mapNumbersToJson(dto.getNumbers()))")
  Ticket toEntity(TicketCreateDto dto);

  @Mapping(target = "numbers", expression = "java(mapJsonToNumbers(ticket.getData()))")
  @Mapping(target = "drawId", source = "draw.id")
  TicketResponseDto toDto(Ticket ticket);

  default String mapNumbersToJson(List<Integer> numbers) {
    try {
      return mapper.writeValueAsString(Map.of("numbers", numbers));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to create JSON", e);
    }
  }

  default List<Integer> mapJsonToNumbers(String json) {
    try {
      return mapper.readValue(json, new TypeReference<Map<String, List<Integer>>>() {})
              .get("numbers");
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse JSON", e);
    }
  }
}
