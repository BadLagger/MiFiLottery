package com.example.lottery.service.validator;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.dto.algorithm.UserSelectedRules;
import com.example.lottery.entity.AlgorithmType;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.repository.LotteryTypeRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Validator {
  private final LotteryTypeMapper lotteryTypeMapper;
  private final LotteryTypeRepository lotteryTypeRepository;

  public void validateTicketForBuyingByDraw(Draw draw) {
    // TODO: Здесь надо забрать тип алгоритма из сервиса LotteryType
    AlgorithmType algorithmType =
        lotteryTypeRepository.findById(draw.getLotteryType().getId()).get().getAlgorithmType();

    if (!algorithmType.isDrawStatusAllowed(draw.getStatus())) {
      throw new ValidationException(
          "Невозможно создать билет для тиража со статусом: " + draw.getStatus());
    }
  }

  public void validateNumbers(List<Integer> numbers, LotteryType lotteryType) {
    AlgorithmRules rules = lotteryTypeMapper.parseRules(lotteryType.getAlgorithmRules());

    // Общие проверки для всех алгоритмов

    if (numbers.size() != rules.getNumberCount()) {
      throw new ValidationException("Должно быть " + rules.getNumberCount() + " чисел");
    }
    // Проверка диапазона чисел
    for (Integer num : numbers) {
      if (num < rules.getMinNumber() || num > rules.getMaxNumber()) {
        throw new ValidationException(
            "Числа должны быть в диапазоне ["
                + rules.getMinNumber()
                + "-"
                + rules.getMaxNumber()
                + "]");
      }
    }
    // Проверка уникальности (если нужно) - !randomRules.isAllowDuplicates() &&
    if (hasDuplicates(numbers)) {
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

  // Вспомогательный метод для проверки дубликатов
  private boolean hasDuplicates(List<Integer> numbers) {
    return numbers.size() != new HashSet<>(numbers).size();
  }

  // методы для проверки особых свойств правил для конкретных типов алгоритмов
  private void validateByFixedPoolRules(FixedPoolRules rules) {
    if (rules.getPoolSize() == 0) {
      throw new ValidationException("Размер пула предсозданных билетов не может быть нулевым");
    }
  }
}
