// InvoiceService.java
package com.example.lottery.service;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.InvoiceStatus;
import com.example.lottery.entity.*;
import com.example.lottery.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceDto createInvoice(Invoice invoice) {
        invoice.setRegisterTime(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.UNPAID);
        Invoice saved = invoiceRepository.save(invoice);
        return toDto(saved);
    }

    public List<InvoiceDto> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status).stream()
                .map((this::toDto))
                .collect(Collectors.toList());
    }

    public Optional<InvoiceDto> getInvoiceById(Long id) {
        return invoiceRepository.findById(id).map(this::toDto);
    }

    public InvoiceDto updateInvoiceStatus(Long id, InvoiceStatus status, int cancelled) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setStatus(status);
        invoice.setCancelled(cancelled);
        return toDto(invoiceRepository.save(invoice));
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

    private InvoiceDto toDto(Invoice invoice) {
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


}

/*
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository repo) {
        this.invoiceRepository = repo;
    }

    public Invoice registerInvoice(Long userId, List<String> ticketData, String paymentLink) {
        Invoice invoice = new Invoice();
        //invoice.setId(UUID.randomUUID());
        invoice.setUserId(userId);
        invoice.setTicketData(ticketData);
        invoice.setRegisterTime(LocalDateTime.now());
        invoice.setPaymentLink(paymentLink);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setCancelled(0);
        invoiceRepository.save(invoice);
        return invoice;
    }

    public Optional<Invoice> getById(Long id) {
        return invoiceRepository.findById(id);
    }

    // Запрос на оплату (POST /api/payments) «блокирует» инвойс – переводит в статус PENDING
    public boolean blockPending(Long invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            if (invoice.getStatus() == InvoiceStatus.UNPAID && invoice.getCancelled() == 0) {
                invoice.setStatus(InvoiceStatus.PENDING);
                invoiceRepository.save(invoice);
                return true;
            }
        }
        return false;
    }

    // Обновить статус при успешной оплате
    public void markPaid(Long invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }
    }


    // Обновить статус при неудачной оплате
    public void markUnpaid(Long invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoiceRepository.save(invoice);
        }
    }

    // Отмена инвойса (если не оплачен и не в Pending)
    public boolean cancelInvoice(Long invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            if (invoice.getCancelled() == 0 && invoice.getStatus() == InvoiceStatus.UNPAID) {
                invoice.setCancelled(1);
                invoice.setStatus(InvoiceStatus.UNPAID);
                invoiceRepository.save(invoice);
                return true;
            }
        }
        return false;
    }

    // Рефанд после завершения тиража (по бизнес-логике)
    public void refundInvoice(Long invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            //возврат ДС на баланс добавить
            invoice.setStatus(InvoiceStatus.REFUNDED);
            invoiceRepository.save(invoice);
        }
    }
}*/
