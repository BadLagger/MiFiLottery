package com.example.lottery.controller;

import com.example.lottery.dto.InvoiceResponseDto;
import com.example.lottery.dto.PaymentCreateDto;
import com.example.lottery.dto.PaymentResponseDto;
import com.example.lottery.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<PaymentResponseDto> process(
            @RequestBody PaymentCreateDto paymentCreateDto,
            @RequestParam String cardNumber,
            @RequestParam String cvc,
            @RequestParam BigDecimal amount
    ) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(paymentCreateDto, cardNumber, cvc, amount));
    }

/*    @GetMapping("/{id}")
    public Optional<PaymentCreateDto> getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }*/
}