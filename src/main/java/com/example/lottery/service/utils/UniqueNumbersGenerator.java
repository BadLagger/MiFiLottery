package com.example.lottery.service.utils;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.Draw;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.CRC32;

import com.example.lottery.repository.PreGeneratedTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueNumbersGenerator {
  private final SecureRandom random;
  private final PreGeneratedTicketRepository preGeneratedTicketRepository;

  public List<Integer> generateNumbers(AlgorithmRules rules, Draw draw) {
    Set<Integer> uniqueNumbers = new HashSet<>();
    do {
    while (uniqueNumbers.size() < rules.getNumberCount()) {
      int num =
          random.nextInt(rules.getMaxNumber() - rules.getMinNumber() + 1) + rules.getMinNumber();
      uniqueNumbers.add(num);
    }

      // TODO: прикрутить проверку на уникальность билета в рамках тиража
      if (preGeneratedTicketRepository.existsByDrawAndNumbersHash(
          draw, crc32Hash(new ArrayList<>(uniqueNumbers)))) {
        uniqueNumbers.clear();
      }
    } while (uniqueNumbers.isEmpty());

    return new ArrayList<>(uniqueNumbers);
  }

  public String crc32Hash(List<Integer> numbers) {
    CRC32 crc32 = new CRC32();
    crc32.update(numbers.stream().sorted().toString().getBytes(StandardCharsets.UTF_8));
    return Long.toHexString(crc32.getValue());
  }
}
