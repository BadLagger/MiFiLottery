package com.example.lottery.controller;

import com.example.lottery.controller.helpers.AuthHelper;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.service.TicketService;
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
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Операции с лотерейными билетами")
public class TicketController {
  private final TicketService ticketService;
  private final AuthHelper authHelper;

  @PreAuthorize("hasRole('NOBODY')")
  @PostMapping("/new")
  public ResponseEntity<TicketResponseDto> createTicketDraft(
      @Valid @RequestBody TicketCreateDto dto) {
    // TODO: ручку оставим на будущее, когда можно будет делать несколько билетов в одном инвойсе

    return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.getTicketDraft(dto));
  }

  @Operation(
      summary = "Получить билет по ID",
      description = "Возвращает информацию о билете по его ID. Только для владельца билета.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "200", description = "Билет найден")
  @ApiResponse(responseCode = "403", description = "Нет доступа к чужому билету")
  @ApiResponse(responseCode = "404", description = "Билет не найден")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{id}")
  public ResponseEntity<TicketResponseDto> getTicket(
      @PathVariable @Parameter(name = "id", description = "ID билета", in = ParameterIn.PATH)
          Long id) {
    Long userId = authHelper.getCurrentUserId();
    return ResponseEntity.ok(ticketService.getTicketById(id, userId));
  }

  @Operation(
      summary = "Получить список всех билетов пользователя",
      description = "Возвращает все билеты текущего пользователя",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "200", description = "Список билетов успешно получен")
  @PreAuthorize("hasRole('USER')")
  @GetMapping
  public ResponseEntity<List<TicketResponseDto>> getUserTickets() {
    Long userId = authHelper.getCurrentUserId();
    return ResponseEntity.ok(ticketService.getUserTickets(userId));
  }
}
