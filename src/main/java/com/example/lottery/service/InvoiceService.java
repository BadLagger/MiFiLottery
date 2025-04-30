// InvoiceService.java
package com.example.lottery.service;

import com.example.lottery.dto.InvoiceStatus;
import com.example.lottery.entity.*;
import com.example.lottery.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository repo) {
        this.invoiceRepository = repo;
    }

    public Invoice registerInvoice(UUID userId, List<String> ticketData, String paymentLink) {
        Invoice inv = new Invoice();
        inv.setId(UUID.randomUUID());
        inv.setUserId(userId);
        inv.setTicketData(ticketData);
        inv.setRegisterTime(LocalDateTime.now());
        inv.setPaymentLink(paymentLink);
        inv.setStatus(InvoiceStatus.UNPAID);
        inv.setCancelled(0);
        invoiceRepository.save(inv);
        return inv;
    }

    public Optional<Invoice> getById(UUID id) {
        return invoiceRepository.findById(id);
    }

    // Запрос на оплату (POST /api/payments) «блокирует» инвойс – переводит в статус PENDING
    public boolean blockPending(UUID invoiceId) {
        Optional<Invoice> opt = invoiceRepository.findById(invoiceId);
        if (opt.isPresent()) {
            Invoice invoice = opt.get();
            if (invoice.getStatus() == InvoiceStatus.UNPAID && invoice.getCancelled() == 0) {
                invoice.setStatus(InvoiceStatus.PENDING);
                invoiceRepository.save(invoice);
                return true;
            }
        }
        return false;
    }

    // Обновить статус при успешной оплате
    public void markPaid(UUID invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }
    }


    // Обновить статус при неудачной оплате
    public void markUnpaid(UUID invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoiceRepository.save(invoice);
        }
    }

    // Отмена инвойса (если не оплачен и не в Pending)
    public boolean cancelInvoice(UUID invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            if (invoice.getCancelled() == 0 && invoice.getStatus() == InvoiceStatus.UNPAID) {
                invoice.setCancelled(1);
                invoice.setStatus(InvoiceStatus.UNPAID); // или специальный статус, если нужно
                invoiceRepository.save(invoice);
                return true;
            }
        }
        return false;
    }

    // Рефанд после завершения тиража (по бизнес-логике)
    public void refundInvoice(UUID invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            invoice.setStatus(InvoiceStatus.REFUNDED);
            invoiceRepository.save(invoice);
        }
    }
}