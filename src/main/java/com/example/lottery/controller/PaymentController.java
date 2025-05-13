package com.example.lottery.controller;

import com.example.lottery.controller.helpers.AuthHelper;
import com.example.lottery.dto.PaymentCreateDto;
import com.example.lottery.dto.PaymentDto;
import com.example.lottery.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Операции, связанные с оплатой билетов")
public class PaymentController {
  private final PaymentService paymentService;
  private final AuthHelper authHelper;

  @Operation(
      summary = "Обработать оплату",
      description =
          "Создаёт платёж по предоставленным данным. Доступно только авторизованным пользователям.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "200", description = "Платёж успешно обработан")
  @ApiResponse(responseCode = "400", description = "Некорректные данные платежа")
  @ApiResponse(responseCode = "403", description = "Доступ запрещён")
  @ApiResponse(responseCode = "404", description = "Счёт не найден")
  @PreAuthorize("hasRole('USER')")
  @PostMapping
  public ResponseEntity<String> process(
      @Valid @RequestBody @Parameter(description = "Данные для обработки платежа", required = true)
          PaymentCreateDto dto) {
    Long userId = authHelper.getCurrentUserId();
    return ResponseEntity.ok().body(paymentService.process(dto, userId));
  }

  @Operation(
      summary = "Получить информацию о платеже",
      description =
          "Возвращает информацию о платеже по его ID. Только для авторизованных пользователей.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "200", description = "Информация о платеже найдена")
  @ApiResponse(responseCode = "403", description = "Доступ запрещён")
  @ApiResponse(responseCode = "404", description = "Платёж не найден")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{id}")
  public ResponseEntity<PaymentDto> getById(
      @PathVariable @Parameter(name = "id", description = "ID платежа") Long id) {

    Long userId = authHelper.getCurrentUserId();

    return ResponseEntity.ok().body(paymentService.getPaymentByIdAndUser(id, userId));
  }
}
