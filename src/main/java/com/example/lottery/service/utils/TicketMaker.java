package com.example.lottery.service.utils;

import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.utils.MapConverter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMaker {
  private final MapConverter mapConverter;

  public Ticket create(Draw draw, List<Integer> numbers) {
    Ticket ticket = new Ticket();
    ticket.setData(mapConverter.mapNumbersToJson(numbers));
    ticket.setDraw(draw);
    ticket.setStatus(Ticket.Status.INGAME);
    return ticket;
  }
}
