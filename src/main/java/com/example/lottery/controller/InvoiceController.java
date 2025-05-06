package com.example.lottery.controller;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping
    public InvoiceDto create(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }

    @GetMapping
    public List<InvoiceDto> getByStatus(@RequestParam Invoice.Status status) {
        return invoiceService.getInvoicesByStatus(status);
    }

    @GetMapping("/{id}")
    public Optional<InvoiceDto> getById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public InvoiceDto updateStatus(
            @PathVariable Long id,
            @RequestParam Invoice.Status status,
            @RequestParam int cancelled
    ) {
        return invoiceService.updateInvoiceStatus(id, status, cancelled);
    }
}
