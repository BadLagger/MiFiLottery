package com.example.lottery.service;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import java.util.List;

public interface TicketService {
  TicketResponseDto createTicket(TicketCreateDto dto, Long userId);

  TicketResponseDto getTicketById(Long id, Long userId);

  List<TicketResponseDto> getUserTickets(Long userId);
}
