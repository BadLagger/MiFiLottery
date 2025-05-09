package com.example.lottery.service;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import java.util.List;

public interface TicketGenerator {

  TicketResponseDto generateTicket();

  List<Integer> generateNumbers();

  boolean supports(AlgorithmRules rules); // Проверяет, подходит ли генератор для данных правил

  void setDraw(Draw draw);

  AlgorithmRules getRules();
}
