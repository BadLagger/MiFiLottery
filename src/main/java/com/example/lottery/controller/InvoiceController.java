package com.example.lottery.controller;

import com.example.lottery.dto.InvoiceRegisterDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> registerInvoice(@RequestBody InvoiceRegisterDto dto) {
        // DTO должен содержать userId, ticketData (массив String)
        Invoice invoice = invoiceService.registerInvoice(dto.getUserId(), dto.getTicketData(), UUID.randomUUID().toString());
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelInvoice(@PathVariable UUID id) {
        boolean cancelled = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(cancelled ? "Invoice cancelled" : "Cannot cancel invoice");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable UUID id) {
        return invoiceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
