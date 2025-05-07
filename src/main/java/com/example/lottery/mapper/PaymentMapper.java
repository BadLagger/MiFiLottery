package com.example.lottery.mapper;

import com.example.lottery.dto.PaymentCreateDto;
import com.example.lottery.dto.PaymentResponseDto;
import com.example.lottery.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "invoiceId", target = "invoice.id")
    @Mapping(target = "id", ignore = true)
    Payment toEntity(PaymentCreateDto dto);

    @Mapping(source = "invoice.id", target = "invoiceId")
    PaymentResponseDto toDto(Payment payment);
}
