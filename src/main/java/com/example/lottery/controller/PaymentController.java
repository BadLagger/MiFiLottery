package com.example.lottery.controller;

import com.example.lottery.dto.PaymentDto;
import com.example.lottery.entity.Payment;
import com.example.lottery.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public PaymentDto process(
            @RequestParam Long invoiceId,
            @RequestParam String cardNumber,
            @RequestParam String cvc,
            @RequestParam BigDecimal amount
    ) {
        return paymentService.processPayment(invoiceId, cardNumber, cvc, amount);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public Optional<PaymentDto> getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }
}


/*
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService service) {
        this.paymentService = service;
    }

    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentDto dto) {
        Payment payment = paymentService.processPayment(
                dto.getInvoiceId(),
                dto.getAmount(),
                dto.getCardNumber(),
                dto.getCvc(),
                dto.getDrawStatus()
        );
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public String checkWork(){
        return "Работает";
    }
}*/
