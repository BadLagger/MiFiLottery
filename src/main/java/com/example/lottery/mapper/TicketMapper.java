package com.example.lottery.mapper;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.utils.MapConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TicketMapper {
  @Autowired protected MapConverter mapConverter;

  @Mapping(target = "data", expression = "java(mapConverter.mapNumbersToJson(dto.getNumbers()))")
  public abstract Ticket toEntity(TicketCreateDto dto);

  @Mapping(target = "numbers", expression = "java(mapConverter.mapJsonToNumbers(ticket.getData()))")
  @Mapping(target = "drawId", source = "draw.id")
  @Mapping(target = "status", source = "status")
  public abstract TicketResponseDto toDto(Ticket ticket);
}
