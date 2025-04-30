package com.example.lottery.service;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;

public interface TicketGenerator {
  Ticket generateTicket(Draw draw);

  boolean supports(AlgorithmRules rules); // Проверяет, подходит ли генератор для данных правил
}
