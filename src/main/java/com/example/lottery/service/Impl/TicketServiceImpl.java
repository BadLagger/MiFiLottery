package com.example.lottery.service.Impl;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.*;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.service.DrawService;
import com.example.lottery.service.TicketGenerator;
import com.example.lottery.service.TicketService;
import com.example.lottery.service.TicketsFactory;
import com.example.lottery.service.validator.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
  private final TicketRepository ticketRepository;
  private final TicketMapper ticketMapper;
  private final DrawService drawService;
  private final Validator validator;
  private final TicketsFactory ticketsFactory;

  //  private final UserService userService; // Заглушка

  // TODO: Не использовать напрямую!! (вместо этого использовать фабрику билетов)
  @Override
  public TicketResponseDto getTicketDraft(TicketCreateDto dto) {
    // Получаем тираж и проверяем его статус
    Draw draw = getDrawById(dto.getDrawId());
    validator.validateForBuyingByDraw(draw);

    // Валидируем выбранные номера от пользователя или генерим черновик билета
    TicketGenerator generator = ticketsFactory.getGenerator(draw);

    return generator.generateTicket();
  }

  @Override
  @Transactional
  public Ticket saveTicket(Ticket ticket) {
    validator.validateForBuyingByDraw(ticket.getDraw());
    return ticketRepository.save(ticket);
  }

  @Override
  public TicketResponseDto getTicketById(Long id, Long userId) {
    return ticketRepository
        .findById(id)
        .filter(ticket -> ticket.getUser().getId().equals(userId))
        .map(ticketMapper::toDto)
        .orElseThrow(() -> new NotFoundException("Билет с ID " + id + " не найден"));
  }

  @Override
  public List<TicketResponseDto> getUserTickets(Long userId) {
    return ticketRepository.findByUserId(userId).stream().map(ticketMapper::toDto).toList();
  }

  @Override
  public List<Long> getTicketIdsByDrawId(Long drawId) {
    return ticketRepository.findAllTicketsByDrawId(drawId).stream().map(Ticket::getId).toList();
  }

  private Draw getDrawById(Long drawId) {
    return drawService.getDrawById(drawId);
  }
}
