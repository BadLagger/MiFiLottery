package com.example.lottery.service.Impl;

import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.*;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.service.DrawService;
import com.example.lottery.service.TicketGenerator;
import com.example.lottery.service.TicketService;
import com.example.lottery.service.TicketsFactory;
import com.example.lottery.service.validator.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
  private final TicketRepository ticketRepository;
  private final TicketMapper ticketMapper;
  private final Validator validator;
  private final TicketsFactory ticketsFactory;
  private final DrawRepository drawRepository;

  //  private final UserService userService; // Заглушка

  //  @Override
  //  public TicketResponseDto getTicketDraft(TicketCreateDto dto) {
  //    // Получаем тираж и проверяем его статус
  //    Draw draw =
  //        drawService
  //            .findById(dto.getDrawId())
  //            .orElseThrow(
  //                () -> new NotFoundException("Тираж с ID " + dto.getDrawId() + " не найден"));
  //
  //    validator.validateTicketForBuyingByDrawId(dto.getDrawId());
  //
  //    // Валидируем выбранные номера от пользователя или генерим черновик билета
  //    TicketGenerator generator = ticketsFactory.getGenerator(draw);
  //    if (generator instanceof UserSelectedTicketGenerator) {
  //      validator.validateNumbers(dto.getNumbers(), draw.getLotteryType());
  //      return ((UserSelectedTicketGenerator) generator).createDraft(dto.getNumbers());
  //    }
  //    return generator.generateTicket();
  //  }

  @Override
  public TicketResponseDto getTicketDraft(Long drawId, List<Integer> numbers) {
    Draw draw = getDrawById(drawId);
    // Получаем тираж и проверяем его статус
    validator.validateTicketForBuyingByDrawId(draw);

    // Валидируем выбранные номера от пользователя или генерим черновик билета
    TicketGenerator generator = ticketsFactory.getGenerator(draw);
    if (generator instanceof UserSelectedTicketGenerator) {
      validator.validateNumbers(numbers, draw);
      return ((UserSelectedTicketGenerator) generator).createDraft(numbers);
    }
    return generator.generateTicket();
  }

  @Override
  @Transactional
  public Ticket saveTicket(Ticket ticket) {
    validator.validateTicketForBuyingByDrawId(ticket.getDraw());
    // TODO: проверки перед сохранением билета на корректность (наличие всех параметров)
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

  //  @Transactional
  //  public void generateTicketsPoolForDraw(Draw draw) {
  //    // Получаем генератор через фабрику
  //    TicketGenerator generator = ticketsFactory.getGenerator(draw);
  //
  //    // Проверяем, что это именно FixedPool генератор
  //    if (!(generator instanceof FixedPoolTicketGenerator)) {
  //      throw new IllegalStateException(
  //          "Метод initPoolForDraw поддерживает только FixedPool тиражи. Получен: "
  //              + generator.getClass().getSimpleName());
  //    }
  //
  //    // Получаем правила для проверки размера пула
  //    FixedPoolRules fixedPoolRules = (FixedPoolRules) generator.getRules();
  //
  //    // Генерируем пул билетов
  //    List<PreGeneratedTicket> poolTickets = new ArrayList<>();
  //    for (int i = 0; i < fixedPoolRules.getPoolSize(); i++) {
  //      // Используем генератор для создания билетов
  //      List<Integer> numbers = generator.generateNumbers();
  //
  //      PreGeneratedTicket pgTicket = new PreGeneratedTicket();
  //      pgTicket.setDraw(draw);
  //      pgTicket.setNumbers(JsonMapper.mapNumbersToJson(numbers));
  //      poolTickets.add(pgTicket);
  //    }
  //    // Сохраняем пул в БД
  //    preGeneratedRepo.saveAll(poolTickets);
  //  }
  private Draw getDrawById(Long id) {
    return drawRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Тираж с ID = " + id + " не найден"));
  }
}
