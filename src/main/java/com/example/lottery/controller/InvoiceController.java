package com.example.lottery.controller;

import com.example.lottery.controller.helpers.AuthHelper;
import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Операции, связанные с инвойсами и оплатой билетов")
public class InvoiceController {
  private final InvoiceService invoiceService;
  private final AuthHelper authHelper;

  @Operation(
      summary = "Создать инвойс для билета",
      description =
          "Создаёт новый инвойс на основе данных билета. Доступно только авторизованным пользователям.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "201", description = "Инвойс успешно создан")
  @ApiResponse(responseCode = "400", description = "Некорректные данные")
  @ApiResponse(responseCode = "403", description = "Доступ запрещён")
  @PreAuthorize("hasRole('USER')")
  @PostMapping
  public ResponseEntity<TicketInInvoiceDto> create(
      @Valid @RequestBody @Parameter(description = "Данные для генерации инвойса", required = true)
          TicketCreateDto dto) {
    Long userId = authHelper.getCurrentUserId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(invoiceService.createInvoice(dto, userId));
  }

  @Operation(
      summary = "Получить список инвойсов по статусу",
      description =
          "Возвращает все инвойсы текущего пользователя с указанным статусом. Только для авторизованных.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "200", description = "Список инвойсов успешно получен")
  @ApiResponse(responseCode = "403", description = "Доступ запрещён")
  @PreAuthorize("hasRole('USER')")
  @GetMapping
  public List<InvoiceDto> getByStatus(
      @RequestParam
          @Parameter(
              name = "status",
              description = "Статус инвойса (UNPAID, PENDING, PAID)",
              in = ParameterIn.QUERY)
          Invoice.Status status) {
    Long userId = authHelper.getCurrentUserId();
    return invoiceService.getInvoicesByStatus(status, userId);
  }

  @Operation(
      summary = "Получить инвойс по ID",
      description = "Возвращает информацию о конкретном инвойсе. Только для владельца инвойса.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "200", description = "Инвойс найден")
  @ApiResponse(responseCode = "403", description = "Доступ к чужому инвойсу запрещён")
  @ApiResponse(responseCode = "404", description = "Инвойс не найден")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{id}")
  public InvoiceDto getById(
      @PathVariable @Parameter(name = "id", description = "ID инвойса", in = ParameterIn.PATH)
          Long id) {
    Long userId = authHelper.getCurrentUserId();
    return invoiceService.getInvoiceById(id, userId);
  }
}
