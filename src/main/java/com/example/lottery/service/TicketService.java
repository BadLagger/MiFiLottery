package com.example.lottery.service;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import java.util.List;

public interface TicketService {

  Ticket saveTicket(Ticket ticket);

  TicketResponseDto getTicketDraft(TicketCreateDto dto);

  TicketResponseDto getTicketById(Long id, Long userId);

  List<TicketResponseDto> getUserTickets(Long userId);

  List<Long> getTicketIdsByDrawId(Long drawId);

  void generateTicketsPoolForDraw(Draw draw);
}
