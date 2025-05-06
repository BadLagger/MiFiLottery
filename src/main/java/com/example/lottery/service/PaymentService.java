package com.example.lottery.service;

import com.example.lottery.dto.PaymentDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.Payment;
import com.example.lottery.mapper.PaymentMapper;
import com.example.lottery.mock.MockPaymentProcessor;
import com.example.lottery.repository.InvoiceRepository;
import com.example.lottery.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final MockPaymentProcessor mockPaymentProcessor;
    private final InvoiceService invoiceService;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentDto processPayment(Long invoiceId, String cardNumber, String cvc, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        // Проверка: если инвойс оплачен, отменен, либо его тираж не активен — запретить
        if (invoice.getStatus() != Invoice.Status.UNPAID) {
            throw new IllegalStateException("Invoice is not available for payment!");
        }

        // ToDo: Проверить статус тиража по ticketData через DrawService

        invoiceService.setPending(invoiceId);

        Payment.Status status = mockPaymentProcessor.process(cardNumber, cvc);
        Payment payment = new Payment(
                null,
                invoice,
                amount,
                LocalDateTime.now(),
                status
        );
        Payment saved = paymentRepository.save(payment);

        // Завершить процесс
        if (status == Payment.Status.SUCCESS) {
            invoiceService.setPaid(invoiceId);

            //toDo Создать билет для пользователя

        } else {
            invoiceService.setUnpaid(invoiceId);
        }
        return paymentMapper.toDto(saved);
    }

    public Optional<PaymentDto> getPaymentById(Long id) {
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    /*private PaymentDto toDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getInvoice() == null ? null : payment.getInvoice().getId(),
                payment.getAmount(),
                payment.getPaymentTime(),
                payment.getStatus()
        );
    }*/

}