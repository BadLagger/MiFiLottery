package com.example.lottery.service.Impl;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;
import com.example.lottery.entity.User;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.service.DrawService;
import com.example.lottery.service.TicketService;
import java.util.HashSet;
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
  private final LotteryTypeMapper lotteryTypeMapper;

  //  private final UserService userService; // Заглушка

  @Override
  @Transactional
  public TicketResponseDto createTicket(TicketCreateDto dto, Long userId) {
    // 1. Получаем тираж и проверяем его статус
    Draw draw =
        drawService
            .findById(dto.getDrawId())
            .orElseThrow(
                () -> new NotFoundException("Тираж с ID " + dto.getDrawId() + " не найден"));
    validateDrawStatus(draw);

    // 2. Валидируем числа на основе правил лотереи
    validateNumbers(dto.getNumbers(), draw.getLotteryType());

    // 3. Создаём билет
    User user = new User(userId); // Заглушка (реальная реализация через UserService)
    Ticket ticket = ticketMapper.toEntity(dto);
    ticket.setDraw(draw);
    ticket.setUser(user);
    ticket.setStatus(Ticket.Status.INGAME);

    return ticketMapper.toDto(ticketRepository.save(ticket));
  }

  private void validateNumbers(List<Integer> numbers, LotteryType lotteryType) {
    AlgorithmRules rules = lotteryTypeMapper.parseRules(lotteryType.getAlgorithmRules());

    if (rules instanceof RandomUniqueRules randomRules) {
      // Проверка для "5 из 36" (или других RandomUnique)
      if (numbers.size() != randomRules.getNumberCount()) {
        throw new ValidationException("Должно быть " + randomRules.getNumberCount() + " чисел");
      }

      // Проверка диапазона чисел
      for (Integer num : numbers) {
        if (num < randomRules.getMinNumber() || num > randomRules.getMaxNumber()) {
          throw new ValidationException(
              "Числа должны быть в диапазоне ["
                  + randomRules.getMinNumber()
                  + "-"
                  + randomRules.getMaxNumber()
                  + "]");
        }
      }

      // Проверка уникальности (если нужно) - !randomRules.isAllowDuplicates() &&
      if (hasDuplicates(numbers)) {
        throw new ValidationException("Числа должны быть уникальны");
      }

    } else if (rules instanceof FixedPoolRules) {
      // TODO: Логика для предсозданных билетов (реализуем позже)
      throw new UnsupportedOperationException("FixedPool пока не поддерживается");
    }
  }

  // Вспомогательный метод для проверки дубликатов
  private boolean hasDuplicates(List<Integer> numbers) {
    return numbers.size() != new HashSet<>(numbers).size();
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
