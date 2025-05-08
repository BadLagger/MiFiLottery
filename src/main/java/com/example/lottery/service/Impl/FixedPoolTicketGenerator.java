package com.example.lottery.service.Impl;

import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.entity.PreGeneratedTicket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.PreGeneratedTicketRepository;
import com.example.lottery.service.AbstractTicketGenerator;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FixedPoolTicketGenerator extends AbstractTicketGenerator {
  private final PreGeneratedTicketRepository preGeneratedRepo;

  public FixedPoolTicketGenerator(
      LotteryTypeMapper lotteryTypeMapper,
      TicketMapper ticketMapper,
      UniqueNumbersGenerator uniqueNumbersGenerator,
      PreGeneratedTicketRepository preGeneratedRepo) {
    super(lotteryTypeMapper, ticketMapper, uniqueNumbersGenerator);
    this.preGeneratedRepo = preGeneratedRepo;
  }

  @Override
  public TicketResponseDto generateTicket() {
    // 1. Пытаемся взять билет из пула
    Optional<PreGeneratedTicket> poolTicket = preGeneratedRepo.findFirstByDrawAndIssuedFalse(draw);

    if (poolTicket.isPresent()) {
      PreGeneratedTicket pgTicket = poolTicket.get();
      pgTicket.setIssued(true); // TODO: присвоить false если не купили
      preGeneratedRepo.save(pgTicket);

      return createDraft(pgTicket.getNumbers());
    }

    // 2. Если пуст — генерируем новый билет
    log.debug("Пул пуст, генерируем новый билет.");
    return createDraft(generateNumbers());
  }

  @Override
  public boolean supports(AlgorithmRules rules) {
    return rules instanceof FixedPoolRules;
  }
}
