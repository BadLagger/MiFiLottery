package com.example.lottery.controller;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TicketInInvoiceDto> create(@Valid @RequestBody TicketCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(dto));
    }

    @GetMapping
    public List<InvoiceDto> getByStatus(@RequestParam Invoice.Status status) {
        return invoiceService.getInvoicesByStatus(status);
    }

    @GetMapping("/{id}")
    public InvoiceDto getById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
//        return JsonMapper.fromJson(invoiceService.getInvoiceById(id).getTicketData(), TicketResponseDto.class);
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
