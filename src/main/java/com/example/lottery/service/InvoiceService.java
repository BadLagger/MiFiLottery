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
}