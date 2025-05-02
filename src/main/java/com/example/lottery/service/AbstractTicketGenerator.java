package com.example.lottery.service;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.UserSelectedRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractTicketGenerator implements TicketGenerator {
  @Setter protected Draw draw;
  protected LotteryTypeMapper lotteryTypeMapper;

  public AbstractTicketGenerator(
      LotteryTypeMapper lotteryTypeMapper, UniqueNumbersGenerator uniqueNumbersGenerator) {
    this.lotteryTypeMapper = lotteryTypeMapper;
    this.uniqueNumbersGenerator = uniqueNumbersGenerator;
  }

  protected UniqueNumbersGenerator uniqueNumbersGenerator;

  protected AlgorithmRules getRules() {
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
}
