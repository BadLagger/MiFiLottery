package com.example.lottery.mapper;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "ticketData", qualifiedByName = "ticketDataFromJson")
  InvoiceDto toDto(Invoice invoice);

  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "ticketData", expression = "java(JsonMapper.toJson(dto.getTicketData()))")
  Invoice toEntity(InvoiceDto dto);

  @Named("ticketDataFromJson")
  default TicketCreateDto ticketDataFromJson(String json) {
    return JsonMapper.fromJson(json, TicketCreateDto.class);
  }
}
