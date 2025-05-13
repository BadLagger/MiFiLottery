package com.example.lottery.service;

import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.PreGeneratedTicket;
import com.example.lottery.mapper.JsonMapper;
import com.example.lottery.repository.PreGeneratedTicketRepository;
import com.example.lottery.service.Impl.FixedPoolTicketGenerator;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketPoolService {
  private final TicketsFactory ticketsFactory;
  private final PreGeneratedTicketRepository preGeneratedRepo;

  @Transactional
  public void generateTicketsPoolForDraw(Draw draw) {

    // Получаем генератор через фабрику
    TicketGenerator generator = ticketsFactory.getGenerator(draw);

    // Проверяем, что это именно FixedPool генератор
    if (!(generator instanceof FixedPoolTicketGenerator)) {
      throw new IllegalStateException(
          "Метод initPoolForDraw поддерживает только FixedPool тиражи. Получен: "
              + generator.getClass().getSimpleName());
    }

    // Получаем правила для проверки размера пула
    FixedPoolRules fixedPoolRules = (FixedPoolRules) generator.getRules();

    // Генерируем пул билетов
    List<PreGeneratedTicket> poolTickets = new ArrayList<>();
    for (int i = 0; i < fixedPoolRules.getPoolSize(); i++) {
      // Используем генератор для создания билетов
      List<Integer> numbers = generator.generateNumbers().stream().sorted().toList();

      PreGeneratedTicket pgTicket = new PreGeneratedTicket();
      pgTicket.setDraw(draw);
      pgTicket.setNumbers(JsonMapper.mapNumbersToJson(numbers));
      poolTickets.add(pgTicket);
    }
    // Сохраняем пул в БД
    preGeneratedRepo.saveAll(poolTickets);
  }
}
