package com.example.lottery.mapper;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = JsonMapper.class)
public interface TicketMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "data", expression = "java(JsonMapper.mapNumbersToJson(dto.getNumbers()))")
  Ticket toEntity(TicketCreateDto dto);

  @Mapping(target = "numbers", expression = "java(JsonMapper.mapJsonToNumbers(ticket.getData()))")
  @Mapping(target = "drawId", source = "draw.id")
  @Mapping(target = "status", source = "status")
  TicketResponseDto toDto(Ticket ticket);
}
