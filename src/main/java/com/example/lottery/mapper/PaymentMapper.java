package com.example.lottery.mapper;

import com.example.lottery.dto.PaymentDto;
import com.example.lottery.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mappings({
            @Mapping(source = "invoice.id", target = "invoiceId")
    })
    PaymentDto toDto(Payment payment);

    // В обратном преобразовании invoice нужно селектить вручную
    @Mappings({
            @Mapping(target = "invoice", ignore = true)
    })
    Payment toEntity(PaymentDto dto);
}

/*
@Component
public class PaymentMapper {
    public PaymentDto toDto(Payment payment) {
        if (payment == null) return null;
        return new PaymentDto(
                payment.getId(),
                payment.getInvoice() == null ? null : payment.getInvoice().getId(),
                payment.getAmount(),
                payment.getPaymentTime(),
                payment.getStatus()
        );
    }

    public Payment toEntity(PaymentDto dto) {
        if (dto == null) return null;
        Payment entity = new Payment();
        entity.setId(dto.getId());
        // invoice сетится вручную в сервисе/контроллере через invoiceRepo.findById(dto.getInvoiceId())
        entity.setAmount(dto.getAmount());
        entity.setPaymentTime(dto.getPaymentTime());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
*/