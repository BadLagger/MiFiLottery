package com.example.lottery.service.Impl;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.entity.PreGeneratedTicket;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.PreGeneratedTicketRepository;
import com.example.lottery.service.AbstractTicketGenerator;
import com.example.lottery.service.utils.TicketMaker;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class FixedPoolTicketGenerator extends AbstractTicketGenerator {
  private final PreGeneratedTicketRepository preGeneratedRepo;
  private final TicketMapper ticketMapper;

  public FixedPoolTicketGenerator(
      LotteryTypeMapper lotteryTypeMapper,
      UniqueNumbersGenerator uniqueNumbersGenerator,
      PreGeneratedTicketRepository preGeneratedRepo,
      TicketMapper ticketMapper,
      TicketMaker ticketMaker) {
    super(lotteryTypeMapper, uniqueNumbersGenerator);
    this.preGeneratedRepo = preGeneratedRepo;
    this.ticketMapper = ticketMapper;
    this.ticketMaker = ticketMaker;
  }

  private final TicketMaker ticketMaker;

  @Override
  public Ticket generateTicket() {
    // 1. Пытаемся взять билет из пула
    Optional<PreGeneratedTicket> poolTicket = preGeneratedRepo.findFirstByDrawAndIssuedFalse(draw);

    if (poolTicket.isPresent()) {
      PreGeneratedTicket pgTicket = poolTicket.get();
      pgTicket.setIssued(true); // TODO: присвоить false если не купили
      preGeneratedRepo.save(pgTicket);

      return createTicketFromPool(pgTicket.getNumbers());
    }

    // 2. Если пуст — генерируем новый билет
    return generateNewTicket();
  }

  private Ticket createTicketFromPool(String numbersJson) {
    List<Integer> numbers = ticketMapper.mapJsonToNumbers(numbersJson);
    return ticketMaker.create(draw, numbers);
  }

  private Ticket generateNewTicket() {
    return ticketMaker.create(draw, generateNumbers());
  }

  @Override
  public boolean supports(AlgorithmRules rules) {
    return rules instanceof FixedPoolRules;
  }
}
