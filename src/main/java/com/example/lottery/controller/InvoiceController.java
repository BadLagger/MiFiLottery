package com.example.lottery.controller;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.service.InvoiceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
  private final InvoiceService invoiceService;

  @PostMapping
  public ResponseEntity<TicketInInvoiceDto> create(@Valid @RequestBody TicketCreateDto dto) {
    // TODO: add User
    Long userId = 1L;
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(invoiceService.createInvoice(dto, userId));
  }

  @GetMapping
  public List<InvoiceDto> getByStatus(@RequestParam Invoice.Status status) {
    // TODO: add User
    Long userId = 1L;
    return invoiceService.getInvoicesByStatus(status, userId);
  }

  @GetMapping("/{id}")
  public InvoiceDto getById(@PathVariable Long id) {
    // TODO: add User
    Long userId = 1L;
    return invoiceService.getInvoiceById(id, userId);
    //        return JsonMapper.fromJson(invoiceService.getInvoiceById(id).getTicketData(),
    // TicketResponseDto.class);
  }
}
