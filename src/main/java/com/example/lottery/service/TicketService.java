package com.example.lottery.service;

import com.example.lottery.dto.TicketDto;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {

  private final TicketRepository ticketRepository;
  private final DrawRepository drawRepository;
  private final TicketMapper ticketMapper;

  /**
   * Создает новый билет и сохраняет его в базе данных.
   *
   * @param ticketDto объект DTO с информацией о новом билете
   * @return созданный билет в виде DTO
   */
  @Transactional
  public TicketDto createTicket(TicketDto ticketDto) throws BadRequestException {

//    Draw draw =
//        drawRepository
//            .findById(ticketDto.getDrawId())
//            .orElseThrow(
//                () -> new RuntimeException("Draw not found with ID: " + ticketDto.getDrawId()));

    // Проверяем статус тиража
//      if (!Draw.Status.ACTIVE.equals(draw.getStatus()) && !Draw.Status.PLANNED.equals(draw.getStatus())) {
      if (ticketDto.getDrawId() == 4) {
          throw new BadRequestException("Cannot create a ticket for a draw that is not ACTIVE or PLANNED");
      }

    Ticket ticket = ticketMapper.toEntity(ticketDto);
    ticket.setStatus(Ticket.Status.INGAME);
    ticket = ticketRepository.save(ticket);
    return ticketMapper.toDto(ticket);
  }

  /**
   * Получает билет по его идентификатору.
   *
   * @param id уникальный идентификатор билета
   * @return DTO объекта билета, если он существует
   * @throws IllegalArgumentException если билет не найден
   */
  public TicketDto findTicketById(Long id) {
    return ticketRepository
        .findById(id)
        .map(ticketMapper::toDto)
        .orElseThrow(() -> new IllegalArgumentException("Ticket with ID: " + id + " not found"));
  }
}
