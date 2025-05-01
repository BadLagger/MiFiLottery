package com.example.lottery.service.Impl;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.AbstractTicketGenerator;
import com.example.lottery.service.TicketGenerator;
import com.example.lottery.service.utils.TicketMaker;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import java.util.*;

import com.example.lottery.service.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RandomUniqueTicketGenerator extends AbstractTicketGenerator {
  private final TicketMaker ticketMaker;
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
