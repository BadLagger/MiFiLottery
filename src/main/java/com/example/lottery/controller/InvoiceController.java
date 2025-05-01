package com.example.lottery.controller;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.InvoiceStatus;
import com.example.lottery.entity.Invoice;
import com.example.lottery.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public InvoiceDto create(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<InvoiceDto> getByStatus(@RequestParam InvoiceStatus status) {
        return invoiceService.getInvoicesByStatus(status);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public Optional<InvoiceDto> getById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public InvoiceDto updateStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status,
            @RequestParam int cancelled
    ) {
        return invoiceService.updateInvoiceStatus(id, status, cancelled);
    }
}


/*
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> registerInvoice(@RequestBody InvoiceDto dto) {
        // DTO должен содержать userId, ticketData (массив String)
        Invoice invoice = invoiceService.registerInvoice(dto.getUserId(), dto.getTicketData(), UUID.randomUUID().toString());
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelInvoice(@PathVariable Long id) {
        boolean cancelled = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(cancelled ? "Invoice cancelled" : "Cannot cancel invoice");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        return invoiceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public String checkWork(){
        return "Работает";
    }
}
*/
