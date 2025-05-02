package com.example.lottery.service.Impl;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.AbstractTicketGenerator;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import org.springframework.stereotype.Service;

@Service
public class UserSelectedTicketGenerator extends AbstractTicketGenerator {

  public UserSelectedTicketGenerator(
      LotteryTypeMapper lotteryTypeMapper,
      UniqueNumbersGenerator uniqueNumbersGenerator,
      LotteryTypeMapper lotteryTypeMapper1) {
    super(lotteryTypeMapper, uniqueNumbersGenerator);
    this.lotteryTypeMapper = lotteryTypeMapper1;
  }

  @Override
  public Ticket generateTicket() {
    // TODO: Для этого типа лотерей билет создаётся на основе выбора пользователя,
    // поэтому этот метод не используется напрямую.
    throw new UnsupportedOperationException(
        "Для UserSelected используется handler создания билета с числами от пользователя");
  }

  @Override
  public boolean supports(AlgorithmRules rules) {
    return rules instanceof RandomUniqueRules;
  }
}
