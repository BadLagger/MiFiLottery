package com.example.lottery.mapper;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
  @Mapping(target = "userId", source = "user.id")
  @Mapping(
      target = "ticketData",
      expression = "java(JsonMapper.fromJson(invoice.getTicketData(), String.class))")
  InvoiceDto toDto(Invoice invoice);

  @Mapping(target = "user.id", source = "userId")
//  @Mapping(target = "ticketData", expression = "java(JsonMapper.toJson(dto.getTicketData()))")
  Invoice toEntity(InvoiceDto dto);
}
