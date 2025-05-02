package com.example.lottery.service.utils;

import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.TicketMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMaker {
  private final TicketMapper ticketMapper;

  public Ticket create(Draw draw, List<Integer> numbers) {
    Ticket ticket = new Ticket();
    ticket.setData(ticketMapper.mapNumbersToJson(numbers));
    ticket.setDraw(draw);
    ticket.setStatus(Ticket.Status.INGAME);
    return ticket;
  }
}
