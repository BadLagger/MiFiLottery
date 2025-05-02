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

  @Override
  public TicketResponseDto getTicketDraft(TicketCreateDto dto) {
    // Получаем тираж и проверяем его статус
    Draw draw =
        drawService
            .findById(dto.getDrawId())
            .orElseThrow(
                () -> new NotFoundException("Тираж с ID " + dto.getDrawId() + " не найден"));

    validator.validateTicketForBuyingByDraw(draw);

    // Валидируем выбранные номера от пользователя или генерим черновик билета
    TicketGenerator generator = ticketsFactory.getGenerator(draw);
    if (generator instanceof UserSelectedTicketGenerator) {
      validator.validateNumbers(dto.getNumbers(), draw.getLotteryType());
    } else if (generator instanceof FixedPoolTicketGenerator) {
      dto.setNumbers(ticketMapper.mapJsonToNumbers(generator.generateTicket().getData()));
    } else {
      dto.setNumbers(generator.generateNumbers());
    }

    TicketResponseDto draftDto = new TicketResponseDto();
    draftDto.setDrawId(draw.getId());
    draftDto.setNumbers(dto.getNumbers());

    return draftDto;
  }

  @Override
  @Transactional
  public void saveTicket(Ticket ticket) {
    validator.validateTicketForBuyingByDraw(ticket.getDraw());
    ticketRepository.save(ticket);
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
}
