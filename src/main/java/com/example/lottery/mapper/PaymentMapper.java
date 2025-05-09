package com.example.lottery.mapper;

import com.example.lottery.dto.PaymentDto;
import com.example.lottery.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
  @Mapping(source = "invoice.id", target = "invoiceId")
  PaymentDto toDto(Payment payment);

  @Mapping(target = "invoice.id", source = "invoiceId")
  Payment toEntity(PaymentDto dto);
}
