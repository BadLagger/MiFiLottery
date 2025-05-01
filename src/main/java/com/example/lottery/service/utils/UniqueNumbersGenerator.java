package com.example.lottery.service.utils;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.Draw;
import java.security.SecureRandom;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueNumbersGenerator {
  private final SecureRandom random;

  public List<Integer> generateNumbers(AlgorithmRules rules, Draw draw) {
    Set<Integer> uniqueNumbers = new HashSet<>();
    while (uniqueNumbers.size() < rules.getNumberCount()) {
      int num =
          random.nextInt(rules.getMaxNumber() - rules.getMinNumber() + 1) + rules.getMinNumber();
      uniqueNumbers.add(num);
    }

    // TODO: прикрутить проверку на уникальность билета в БД, по типу Рулс определять в какой репе
    // искать дубли в рамках тиража

    return uniqueNumbers.stream().sorted().toList();
  }
}
