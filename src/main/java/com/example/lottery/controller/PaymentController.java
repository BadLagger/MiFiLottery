package com.example.lottery.controller;

import com.example.lottery.dto.PaymentCreateDto;
import com.example.lottery.dto.PaymentDto;
import com.example.lottery.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<String> process(@Valid @RequestBody PaymentCreateDto dto) {
        // TODO: add User
        Long userId = 1L;
        return ResponseEntity.ok()
                .body(paymentService.process(dto, userId));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public Optional<PaymentDto> getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }
}