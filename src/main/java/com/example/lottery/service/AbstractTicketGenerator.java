package com.example.lottery.service;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.dto.algorithm.UserSelectedRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class AbstractTicketGenerator implements TicketGenerator {
  protected Draw draw;
  protected LotteryTypeMapper lotteryTypeMapper;
  protected UniqueNumbersGenerator uniqueNumbersGenerator;

  @Override
  public void setDraw(Draw draw) {
    this.draw = draw;
  }

  protected AlgorithmRules getRules() {
    // Общая логика получения правил для всех генераторов
    LotteryType lotteryType = draw.getLotteryType();
    return lotteryTypeMapper.parseRules(lotteryType.getAlgorithmRules());
  }

  public List<Integer> generateNumbers() {
    // Общая логика генерации чисел для всех генераторов
    if (this.getRules() instanceof UserSelectedRules)
      throw new IllegalArgumentException("В этом типе тиража пользователь выбирает числа самостоятельно");
    return uniqueNumbersGenerator.generateNumbers(this.getRules(), draw);
  }
}
