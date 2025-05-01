package com.example.lottery.service.Impl;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.TicketGenerator;
import com.example.lottery.service.utils.TicketMaker;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RandomUniqueTicketGenerator implements TicketGenerator {
  private final LotteryTypeMapper lotteryTypeMapper;
  private final TicketMaker ticketMaker;
  private final UniqueNumbersGenerator uniqueNumbersGenerator;

  @Override
  public Ticket generateTicket(Draw draw) {
    // 1. Проверяем статус тиража
    if (draw.getStatus() != DrawStatus.PLANNED && draw.getStatus() != DrawStatus.ACTIVE) {
      throw new IllegalStateException(
          "Нельзя генерировать билеты для тиража со статусом: " + draw.getStatus());
    }

    // 2. Получаем правила из типа лотереи
    AlgorithmRules rules = lotteryTypeMapper.parseRules(draw.getLotteryType().getAlgorithmRules());

//    RandomUniqueRules randomUniqueRules = (RandomUniqueRules) rules;
    if (!(supports(rules))) {
      throw new IllegalArgumentException(
          "Генератор не поддерживает правила типа: " + rules.getClass().getSimpleName());
    }

    // 3. Генерируем числа
    List<Integer> numbers = uniqueNumbersGenerator.generateNumbers(rules, draw);

    // 4. Создаём (но не сохраняем) билет с привязкой к тиражу
    return ticketMaker.create(draw, numbers);
  }

  // Остальные методы без изменений
  @Override
  public boolean supports(AlgorithmRules rules) {
    return rules instanceof RandomUniqueRules;
  }
}
