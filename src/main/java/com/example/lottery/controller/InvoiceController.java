package com.example.lottery.controller;

import com.example.lottery.dto.InvoiceCreateDto;
import com.example.lottery.dto.InvoiceResponseDto;
import com.example.lottery.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    // Ручка для создания инвойса
    @PostMapping
    public ResponseEntity<InvoiceResponseDto> createInvoice(@RequestBody InvoiceCreateDto invoiceCreateDto) {  //Система инициирует регистрацию инвойса
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(invoiceCreateDto));
    }

    // Ручка для отмены всех неоплаченных инвойсов по завершённому тиражу. @param drawId - ID завершенного тиража
    @PostMapping("/cancel-unpaid-after-draw/{drawId}")
    public ResponseEntity<String> cancelUnpaidAfterDraw(@PathVariable Long drawId) {
        invoiceService.cancelUnpaidAfterDraw(drawId);
        return ResponseEntity.ok("Unpaid invoices for draw " + drawId + " have been cancelled.");
    }
}

//-----------------------------------------------------------------------------------------------
   /* @GetMapping
    public List<InvoiceResponseDto> getByStatus(@RequestParam InvoiceStatus status) {
        return invoiceService.getInvoicesByStatus(status);
    }

    @GetMapping("/{id}")
    public Optional<InvoiceResponseDto> getById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PatchMapping("/{id}/status")
    public InvoiceResponseDto updateStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status,
            @RequestParam int cancelled
    ) {
        return invoiceService.updateInvoiceStatus(id, status, cancelled);
    }
}
*/
