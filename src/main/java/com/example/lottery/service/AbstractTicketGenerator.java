package com.example.lottery.service;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.UserSelectedRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;
import com.example.lottery.entity.User;
import com.example.lottery.mapper.JsonMapper;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.service.Impl.UserSelectedTicketGenerator;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import java.util.Collections;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractTicketGenerator implements TicketGenerator {
  @Setter protected Draw draw;
  protected LotteryTypeMapper lotteryTypeMapper;
  protected TicketMapper ticketMapper;

  public AbstractTicketGenerator(
      LotteryTypeMapper lotteryTypeMapper,
      TicketMapper ticketMapper,
      UniqueNumbersGenerator uniqueNumbersGenerator) {
    this.lotteryTypeMapper = lotteryTypeMapper;
    this.ticketMapper = ticketMapper;
    this.uniqueNumbersGenerator = uniqueNumbersGenerator;
  }

  protected UniqueNumbersGenerator uniqueNumbersGenerator;

  @Override
  public AlgorithmRules getRules() {
    // Общая логика получения правил для всех генераторов
    LotteryType lotteryType = draw.getLotteryType();
    return lotteryTypeMapper.parseRules(lotteryType.getAlgorithmRules());
  }

  public List<Integer> generateNumbers() {
    // Общая логика генерации чисел для всех генераторов
    if (this.getRules() instanceof UserSelectedRules)
      throw new IllegalArgumentException(
          "В этом типе тиража пользователь выбирает числа самостоятельно");
    return uniqueNumbersGenerator.generateNumbers(this.getRules(), draw);
  }

  protected TicketResponseDto createDraft(List<Integer> numbers) {
    var draft = new TicketResponseDto();
    draft.setDrawId(draw.getId());
    if (getRules().isSorted()) {
      Collections.sort(numbers); // Все числа в наборе по умолчанию отсортированы
    }
    draft.setNumbers(numbers);
    return draft;
  }

  protected TicketResponseDto createDraft(String numbersJson) {
    List<Integer> numbers = JsonMapper.mapJsonToNumbers(numbersJson);
    return createDraft(numbers);
  }
}
