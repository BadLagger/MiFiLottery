package com.example.lottery.service;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.InvoiceStatus;
import com.example.lottery.dto.PaymentDto;
import com.example.lottery.dto.PaymentStatus;
import com.example.lottery.entity.*;
import com.example.lottery.payment.MockPaymentProcessor;
import com.example.lottery.repository.InvoiceRepository;
import com.example.lottery.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final MockPaymentProcessor mockPaymentProcessor;
    private final InvoiceService invoiceService;

    @Transactional
    public PaymentDto processPayment(Long invoiceId, String cardNumber, String cvc, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        // Проверка: если инвойс оплачен, отменен, либо его тираж не активен — запретить
        if (invoice.getStatus() != InvoiceStatus.UNPAID) {
            throw new IllegalStateException("Invoice is not available for payment!");
        }
        // TODO: Проверить статус тиража по ticketData через DrawService

        invoiceService.setPending(invoiceId);

        PaymentStatus status = mockPaymentProcessor.process(cardNumber, cvc);
        Payment payment = new Payment(
                null,
                invoice,
                amount,
                LocalDateTime.now(),
                status
        );
        Payment saved = paymentRepository.save(payment);

        // Завершить процесс
        if (status == PaymentStatus.SUCCESS) {
            invoiceService.setPaid(invoiceId);
            // Создать билет для пользователя
            // ticketService.createTicketFromInvoice(invoice);
        } else {
            invoiceService.setUnpaid(invoiceId);
        }
        return toDto(saved);
    }

    public Optional<PaymentDto> getPaymentById(Long id) {
        return paymentRepository.findById(id).map(this::toDto);
    }



    private PaymentDto toDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getInvoice() == null ? null : payment.getInvoice().getId(),
                payment.getAmount(),
                payment.getPaymentTime(),
                payment.getStatus()
        );
    }

}





/*
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

    public Payment processPayment(Long invoiceId, BigDecimal amount, String cardNumber, String cvc, DrawStatus drawStatus) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (optionalInvoice.isEmpty()) throw new IllegalArgumentException("Invoice not found!");

        Invoice invoice = optionalInvoice.get();
        // Нельзя отменить, если уже оплачен/рефандед/отменен
        if (invoice.getCancelled() == 1 || invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice already paid or cancelled!");
        }

        // Проверяем статус тиража билета (drawStatus
        if (!"ACTIVE".equals(drawStatus)) {
            throw new IllegalStateException("Draw is not ACTIVE. Payment is not alowed.");
        }

        // Переводим инвойс в статус PENDING (блокируем)
        boolean blocked = invoiceService.blockPending(invoiceId);
        if (!blocked) throw new IllegalStateException("Invoice cannot be blocked or is not UNPAID.");

        // Мок-оплата
        PaymentStatus paymentStatus = mockPaymentProcessor.process(cardNumber, cvc);

        Payment payment = new Payment();
//        payment.setId(UUID.randomUUID());
        payment.setInvoice(invoiceId);
        payment.setAmount(amount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setStatus(paymentStatus);

        paymentRepository.save(payment);

        if (paymentStatus == PaymentStatus.SUCCESS) {
            invoiceService.markPaid(invoiceId);
        } else {
            invoiceService.markUnpaid(invoiceId); // возвращаем обратно
        }
        return payment;
    }
}*/
