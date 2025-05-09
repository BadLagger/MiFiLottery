package com.example.lottery.service.validator;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.dto.algorithm.UserSelectedRules;
import com.example.lottery.entity.AlgorithmType;
import com.example.lottery.entity.Draw;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.service.DrawService;
import com.example.lottery.service.LotteryTypeService;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Validator {
  private final LotteryTypeService lotteryTypeService;
  private final DrawService drawService;

  public void validateForBuyingByDraw(Draw draw) {
    //    Draw draw = drawService.getDrawById(drawId);
    AlgorithmType algorithmType = lotteryTypeService.getAlgorithmTypeByDraw(draw);

    if (!algorithmType.isDrawStatusAllowed(draw.getStatus())) {
      throw new ValidationException(
          "Для тиража %s в статусе %s нельзя создавать билеты", draw.getId(), draw.getStatus());
    }
  }

  public void validateNumbers(List<Integer> numbers, Draw draw) {
    //    Draw draw = drawService.getDrawById(drawId);
    AlgorithmRules rules = lotteryTypeService.getAlgorithmRulesByDraw(draw);

    // Общие проверки для всех алгоритмов

    if (numbers.size() != rules.getNumberCount()) {
      throw new ValidationException("Должно быть " + rules.getNumberCount() + " чисел");
    }
    // Проверка диапазона чисел
    for (Integer num : numbers) {
      if (num < rules.getMinNumber() || num > rules.getMaxNumber()) {
        throw new ValidationException(
            "Числа должны быть в диапазоне [%s-%s]", rules.getMinNumber(), rules.getMaxNumber());
      }
    }
    // Проверка уникальности (если нужно) - !randomRules.isAllowDuplicates() &&
    if (new HashSet<>(numbers).size() != numbers.size()) {
      throw new ValidationException("Числа должны быть уникальны");
    }

    // ----
    // Особые проверки для конкретного типа алгоритма

    if (rules instanceof FixedPoolRules fixedPoolRules) {
      // Проверки для FixedPoolRules
      validateByFixedPoolRules(fixedPoolRules);
    } else if (rules instanceof RandomUniqueRules randomUniqueRules) {
      // Проверки для RandomUniqueRules
    } else if (rules instanceof UserSelectedRules userSelectedRules) {
      // Проверки для UserSelectedRules
    } else {
      throw new IllegalArgumentException(
          "Генератор не поддерживает правила типа: " + rules.getClass().getSimpleName());
    }
  }

  // методы для проверки особых свойств правил для конкретных типов алгоритмов
  private void validateByFixedPoolRules(FixedPoolRules rules) {
    if (rules.getPoolSize() == 0) {
      throw new ValidationException("Размер пула предсозданных билетов не может быть нулевым");
    }
  }
}
