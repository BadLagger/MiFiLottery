package com.example.lottery.service;


import com.example.lottery.dto.PaymentCreateDto;
import com.example.lottery.dto.PaymentResponseDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.Payment;
import com.example.lottery.mapper.PaymentMapper;
import com.example.lottery.payment.MockPaymentProcessor;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.InvoiceRepository;
import com.example.lottery.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final MockPaymentProcessor mockPaymentProcessor;
    private final InvoiceService invoiceService;
    private final PaymentMapper paymentMapper;
    private final DrawRepository drawRepository;

    @Transactional
    public PaymentResponseDto processPayment(PaymentCreateDto paymentCreateDto, String cardNumber, String cvc, BigDecimal amount) throws JsonProcessingException {
        Invoice invoice = invoiceRepository.findById(paymentCreateDto.getInvoiceId()).orElseThrow();

        // Проверка: если инвойс оплачен, отменен, либо его тираж не активен — запретить
        if (invoice.getStatus() != Invoice.InvoiceStatus.UNPAID || invoice.getCancelled() == 1) {
            throw new IllegalStateException("Инвойс нельзя оплатить - статус не UNPAID.");
        }

        if (invoice.getCancelled() == 1) {
            throw new IllegalStateException("Инвойс нельзя оплатить - статус cancelled.");
        }

        /*String ticketDataJson = invoice.getTicketData();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ticketDataJson);
        Long drawId = jsonNode.get("drawId").asLong();
        if (drawRepository.findById(drawId).getStatus() == DrawStatus.CANCELLED) {
            throw new IllegalStateException("Инвойс нельзя оплатить - статус cancelled.");
        }*/

        // ToDo: Проверить статус тиража по ticketData через DrawService

        // Установка статуса PENDING перед оплатой
        invoiceService.setPending(invoice.getId());

        Payment.PaymentStatus status = mockPaymentProcessor.process(cardNumber, cvc);
        Payment payment = new Payment(
                null,
                invoice,
                amount,
                LocalDateTime.now(),
                status
        );
        Payment saved = paymentRepository.save(payment);

        // Завершить процесс
        if (status == Payment.PaymentStatus.SUCCESS) {
            invoiceService.setPaid(invoice.getId());

        } else {
            invoiceService.setUnpaid(invoice.getId());
        }

        return paymentMapper.toDto(saved);
    }


}