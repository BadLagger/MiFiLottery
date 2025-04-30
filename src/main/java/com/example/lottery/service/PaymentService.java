package com.example.lottery.service;

import com.example.lottery.dto.DrawResultDto;
import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.InvoiceStatus;
import com.example.lottery.entity.*;
import com.example.lottery.payment.MockPaymentProcessor;
import com.example.lottery.repository.InvoiceRepository;
import com.example.lottery.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final MockPaymentProcessor mockPaymentProcessor;

    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, InvoiceService invoiceService, MockPaymentProcessor mockPaymentProcessor) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
        this.mockPaymentProcessor = mockPaymentProcessor;
    }

    //String drawStatus поменять на DrawStatus
    public Payment processPayment(UUID invoiceId, BigDecimal amount, String cardNumber, String cvc, DrawStatus drawStatus) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isEmpty()) throw new IllegalArgumentException("Invoice not found!");

        Invoice invoice = optionalInvoice.get();
        // Нельзя отменить, если уже оплачен/рефандед/отменен
        if (invoice.getCancelled() == 1 || invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice already paid or cancelled!");
        }

        // Проверяем статус тиража билета (drawStatus). Операция разрешена только если "ACTIVE".
        if (!"ACTIVE".equals(drawStatus)) {
            throw new IllegalStateException("Draw is not ACTIVE. Payment not allowed.");
        }

        // Переводим инвойс в статус PENDING (блокируем)
        boolean blocked = invoiceService.blockPending(invoiceId);
        if (!blocked) throw new IllegalStateException("Invoice cannot be blocked or is not UNPAID.");

        // Мок-оплата
        DrawResultDto.PaymentStatus paymentStatus = mockPaymentProcessor.process(cardNumber, cvc);

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setInvoiceId(invoiceId);
        payment.setAmount(amount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setStatus(paymentStatus);

        paymentRepository.save(payment);

        if (paymentStatus == DrawResultDto.PaymentStatus.SUCCESS) {
            invoiceService.markPaid(invoiceId);
        } else {
            invoiceService.markUnpaid(invoiceId); // возвращаем обратно
        }
        return payment;
    }
}