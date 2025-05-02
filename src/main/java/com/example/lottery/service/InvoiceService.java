// InvoiceService.java
package com.example.lottery.service;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.InvoiceStatus;
import com.example.lottery.entity.Invoice;
import com.example.lottery.mapper.InvoiceMapper;
import com.example.lottery.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceDto createInvoice(Invoice invoice) {
        invoice.setRegisterTime(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.UNPAID);
        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toDto(saved);

    }

    public List<InvoiceDto> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status).stream()
                .map((invoiceMapper::toDto))
                .collect(Collectors.toList());
    }

    public Optional<InvoiceDto> getInvoiceById(Long id) {
        return invoiceRepository.findById(id).map(invoiceMapper::toDto);
    }

    public InvoiceDto updateInvoiceStatus(Long id, InvoiceStatus status, int cancelled) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setStatus(status);
        invoice.setCancelled(cancelled);
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }

    public void setPending(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        if (invoice.getStatus() == InvoiceStatus.UNPAID) {
            invoice.setStatus(InvoiceStatus.PENDING);
            invoiceRepository.save(invoice);
        }
    }

    public void setPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        if (invoice.getStatus() == InvoiceStatus.PENDING) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }
    }

    public void setUnpaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        if (invoice.getStatus() == InvoiceStatus.PENDING) {
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoiceRepository.save(invoice);
        }
    }

    //toDo Отмена неоплаченных инвойсов после завершения тиража
    public void cancelUnpaidAfterDraw(Long drawId) {
        List<Invoice> unpaid = invoiceRepository.findByStatus(InvoiceStatus.UNPAID);
        for (Invoice invoice : unpaid) {
            invoice.setCancelled(1);
            invoiceRepository.save(invoice);
        }
    }

    /*private InvoiceDto toDto(Invoice invoice) {
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
*/

}