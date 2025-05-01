package com.example.lottery.service;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.mapper.LotteryTypeMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketsFactory {

  private final LotteryTypeMapper lotteryTypeMapper;
  private final Map<String, TicketGenerator> generators;

  public TicketGenerator getGenerator(Draw draw) {
    LotteryType lotteryType = draw.getLotteryType();
    AlgorithmRules rules = lotteryTypeMapper.parseRules(lotteryType.getAlgorithmRules());

    for (TicketGenerator generator : generators.values()) {
      if (generator.supports(rules)) {
        generator.setDraw(draw);
        return generator;
      }
    }
    throw new IllegalArgumentException(
        "Не нашлось подходящего генератора билетов для тиража: " + draw.getId());
  }
}
