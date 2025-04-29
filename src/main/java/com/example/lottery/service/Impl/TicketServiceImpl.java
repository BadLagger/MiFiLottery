package com.example.lottery.service.Impl;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.entity.User;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.service.DrawService;
import com.example.lottery.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
  private final TicketRepository ticketRepository;
  private final TicketMapper ticketMapper;
  private final DrawService drawService;

  //  private final UserService userService; // Заглушка

  @Override
  @Transactional
  public TicketResponseDto createTicket(TicketCreateDto dto, Long userId) {
    // TODO: Добавить проверку уникальности чисел для лотереи "5 из 36"
    Draw draw =
        drawService
            .findById(dto.getDrawId())
            .orElseThrow(
                () -> new NotFoundException("Тираж с ID " + dto.getDrawId() + " не найден"));
    validateDrawStatus(draw);

    User user = new User(1L); // userService.getUserById(userId);
    Ticket ticket = ticketMapper.toEntity(dto);
    ticket.setDraw(draw);
    ticket.setUser(user);
    ticket.setStatus(Ticket.Status.INGAME);

    Ticket savedTicket = ticketRepository.save(ticket);
    return ticketMapper.toDto(savedTicket);
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
    // TODO: Добавить пагинацию
    return ticketRepository.findByUserId(userId).stream().map(ticketMapper::toDto).toList();
  }

  private void validateDrawStatus(Draw draw) {
    if (draw.getStatus() != DrawStatus.ACTIVE && draw.getStatus() != DrawStatus.PLANNED) {
      throw new ValidationException(
          "Невозможно создать билет для тиража со статусом: " + draw.getStatus());
    }
  }
}
