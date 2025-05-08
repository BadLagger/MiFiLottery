package com.example.lottery.service;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.mapper.LotteryTypeMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketsFactory {

  private final LotteryTypeMapper lotteryTypeMapper;
  private final Map<String, TicketGenerator> generators;

  public TicketGenerator getGenerator(Draw draw) {
    LotteryType lotteryType = draw.getLotteryType();
    AlgorithmRules rules = lotteryTypeMapper.parseRules(lotteryType.getAlgorithmRules());
    log.debug("Попытка выбрать подходящий генератор билетов для тиража: {}", lotteryType);
    log.debug("Правила алгоритма: {}", rules);

    for (TicketGenerator generator : generators.values()) {
      if (generator.supports(rules)) {
        generator.setDraw(draw);
        return generator;
      }
    }
    throw new IllegalArgumentException(
        "Не нашлось подходящего генератора билетов для тиража: " + draw.getId());
  }

  public TicketGenerator getGenerator(Long drawId, DrawService drawService) {
    Draw draw =
            drawService
                    .findById(drawId)
                    .orElseThrow(
                            () -> new NotFoundException("Тираж с ID " + drawId + " не найден"));
    return getGenerator(draw);
  }
}
