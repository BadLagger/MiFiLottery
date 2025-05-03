package com.example.lottery.service.Impl;

import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.service.AbstractTicketGenerator;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class RandomUniqueTicketGenerator extends AbstractTicketGenerator {
  public RandomUniqueTicketGenerator(
      LotteryTypeMapper lotteryTypeMapper,
      TicketMapper ticketMapper,
      UniqueNumbersGenerator uniqueNumbersGenerator) {
    super(lotteryTypeMapper, ticketMapper, uniqueNumbersGenerator);
  }

  @Override
  public TicketResponseDto generateTicket() {
    // Создаём (но не сохраняем) билет с привязкой к тиражу
    return createDraft(generateNumbers());
  }

  @Override
  public boolean supports(AlgorithmRules rules) {
    return rules instanceof RandomUniqueRules;
  }
}
