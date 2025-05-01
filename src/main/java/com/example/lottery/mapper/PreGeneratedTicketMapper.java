package com.example.lottery.mapper;

import com.example.lottery.dto.PreGeneratedTicketResponseDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.PreGeneratedTicket;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.utils.MapConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PreGeneratedTicketMapper {

  @Autowired protected MapConverter mapConverter;

  @Mapping(target = "numbers", expression = "java(mapConverter.mapNumbersToJson(dto.getNumbers()))")
  public abstract PreGeneratedTicket toEntity(TicketCreateDto dto);

  @Mapping(target = "drawId", source = "draw.id")
  @Mapping(target = "numbers", expression = "java(mapConverter.mapJsonToNumbers(ticket.getNumbers()))")
  public abstract PreGeneratedTicketResponseDto toDto(PreGeneratedTicket ticket);

  // Преобразование списка DTO в список сущностей
  public List<PreGeneratedTicket> toEntityList(List<TicketCreateDto> dtoList) {
    return dtoList.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
  }
}
