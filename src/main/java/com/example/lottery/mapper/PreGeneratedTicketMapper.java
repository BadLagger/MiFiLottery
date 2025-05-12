package com.example.lottery.mapper;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.PreGeneratedTicket;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = JsonMapper.class)
public interface PreGeneratedTicketMapper {

  @Mapping(target = "numbers", expression = "java(JsonMapper.mapNumbersToJson(dto.getNumbers()))")
  PreGeneratedTicket toEntity(TicketCreateDto dto);

  @Mapping(target = "drawId", source = "draw.id")
  @Mapping(
      target = "numbers",
      expression = "java(JsonMapper.mapJsonToNumbers(ticket.getNumbers()))")
  TicketResponseDto toDto(PreGeneratedTicket ticket);

  // Преобразование списка DTO в список сущностей
  default List<PreGeneratedTicket> toEntityList(List<TicketCreateDto> dtoList) {
    return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
