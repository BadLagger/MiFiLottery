package com.example.lottery.service;

import com.example.lottery.controller.PaymentController;
import com.example.lottery.dto.InvoiceCreateDto;
import com.example.lottery.dto.InvoiceResponseDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.mapper.InvoiceMapper;
import com.example.lottery.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final PaymentController paymentController;

    public InvoiceResponseDto createInvoice(InvoiceCreateDto invoiceCreateDto) {
        Invoice invoice = invoiceMapper.toEntity(invoiceCreateDto);
        invoice.setRegisterTime(LocalDateTime.now());
        invoice.setPaymentLink("");//не используем
        invoice.setStatus(Invoice.InvoiceStatus.UNPAID);
        invoice.setCancelled(0);
        Invoice saved = invoiceRepository.save(invoice);

        return invoiceMapper.toDto(saved);
    }

    //Отмена неоплаченных инвойсов после завершения определенного тиража
    public void cancelUnpaidAfterDraw(Long drawId) {
        List<Invoice> unpaid = invoiceRepository.findUnpaidInvoicesByDrawId(drawId);
        for (Invoice invoice : unpaid) {
            invoice.setCancelled(1);
            invoiceRepository.save(invoice);
        }
    }

    public void setPending(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        if (invoice.getStatus() == Invoice.InvoiceStatus.UNPAID) {
            invoice.setStatus(Invoice.InvoiceStatus.PENDING);
            invoiceRepository.save(invoice);
        }
    }

    public void setPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        if (invoice.getStatus() == Invoice.InvoiceStatus.PENDING) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }
    }

    public void setUnpaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        if (invoice.getStatus() == Invoice.InvoiceStatus.PENDING) {
            invoice.setStatus(Invoice.InvoiceStatus.UNPAID);
            invoiceRepository.save(invoice);
        }
    }


    /*public List<InvoiceResponseDto> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status).stream()
                .map((invoiceMapper::toDto))
                .collect(Collectors.toList());
    }

    public Optional<InvoiceResponseDto> getInvoiceById(Long id) {
        return invoiceRepository.findById(id).map(invoiceMapper::toDto);
    }

    public InvoiceResponseDto updateInvoiceStatus(Long id, InvoiceStatus status, int cancelled) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setStatus(status);
        invoice.setCancelled(cancelled);
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }*/



}