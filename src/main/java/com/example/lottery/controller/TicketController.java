package com.example.lottery.controller;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
  private final TicketService ticketService;

  @PostMapping
  public ResponseEntity<TicketResponseDto> createTicket(@Valid @RequestBody TicketCreateDto dto
      //          , @RequestHeader("Authorization") String token
      ) {
    Long userId = 1L; // authService.getUserIdFromToken(token);
    return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(dto, userId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TicketResponseDto> getTicket(@PathVariable Long id
      //          , @RequestHeader("Authorization") String token
      ) {
    Long userId = 1L; // authService.getUserIdFromToken(token);
    return ResponseEntity.ok(ticketService.getTicketById(id, userId));
  }

  @GetMapping
  public ResponseEntity<List<TicketResponseDto>> getUserTickets(
      //          @RequestHeader("Authorization") String token
      ) {
    Long userId = 1L; // authService.getUserIdFromToken(token);
    return ResponseEntity.ok(ticketService.getUserTickets(userId));
  }
}
