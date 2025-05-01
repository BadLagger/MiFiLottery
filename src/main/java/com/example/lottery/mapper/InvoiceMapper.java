package com.example.lottery.mapper;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {
    public InvoiceDto toDto(Invoice invoice) {
        if (invoice == null) return null;
        return new InvoiceDto(
                invoice.getId(),
                invoice.getUser() == null ? null : invoice.getUser().getId(),
                invoice.getTicketData(),
                invoice.getRegisterTime(),
                invoice.getPaymentLink(),
                invoice.getStatus(),
                invoice.getCancelled()
        );
    }

    public Invoice toEntity(InvoiceDto dto) {
        if (dto == null) return null;
        Invoice entity = new Invoice();
        entity.setId(dto.getId());
        // user вручную сетится в сервисе/контроллере через userRepository.findById(dto.getUserId())
        entity.setTicketData(dto.getTicketData());
        entity.setRegisterTime(dto.getRegisterTime());
        entity.setPaymentLink(dto.getPaymentLink());
        entity.setStatus(dto.getStatus());
        entity.setCancelled(dto.getCancelled());
        return entity;
    }
}