package com.example.lottery.service.Impl;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.AbstractTicketGenerator;
import com.example.lottery.service.utils.TicketMaker;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import com.example.lottery.service.validator.Validator;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class RandomUniqueTicketGenerator extends AbstractTicketGenerator {
  private final TicketMaker ticketMaker;

  public RandomUniqueTicketGenerator(
      LotteryTypeMapper lotteryTypeMapper,
      UniqueNumbersGenerator uniqueNumbersGenerator,
      TicketMaker ticketMaker,
      UniqueNumbersGenerator uniqueNumbersGenerator1,
      Validator validator) {
    super(lotteryTypeMapper, uniqueNumbersGenerator);
    this.ticketMaker = ticketMaker;
    this.uniqueNumbersGenerator = uniqueNumbersGenerator1;
    this.validator = validator;
  }

  private final UniqueNumbersGenerator uniqueNumbersGenerator;
  private final Validator validator;

  @Override
  public Ticket generateTicket() {

    // Создаём (но не сохраняем) билет с привязкой к тиражу
    return ticketMaker.create(draw, generateNumbers());
  }

  @Override
  public boolean supports(AlgorithmRules rules) {
    return rules instanceof RandomUniqueRules;
  }
}
