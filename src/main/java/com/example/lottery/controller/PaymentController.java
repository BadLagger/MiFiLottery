package com.example.lottery.controller;

import com.example.lottery.dto.PaymentRequestDto;
import com.example.lottery.entity.Payment;
import com.example.lottery.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService service) {
        this.paymentService = service;
    }

    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentRequestDto dto) {
        Payment payment = paymentService.processPayment(
                dto.getInvoiceId(),
                dto.getAmount(),
                dto.getCardNumber(),
                dto.getCvc(),
                dto.getDrawStatus()
        );
        return ResponseEntity.ok(payment);
    }
}