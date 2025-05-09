package com.example.lottery.controller;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.entity.User;
import com.example.lottery.mapper.JsonMapper;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.service.Impl.TicketServiceImpl;
import com.example.lottery.service.TicketGenerator;
import com.example.lottery.service.TicketPoolService;
import com.example.lottery.service.TicketService;
import com.example.lottery.service.TicketsFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/generator")
@RequiredArgsConstructor
public class GeneratorTestController {
  private final TicketMapper ticketMapper;
  private final DrawRepository drawRepository;
  private final TicketRepository ticketRepository;
  private final TicketsFactory ticketsFactory;
  private final TicketPoolService ticketPoolService;
  private final TicketServiceImpl ticketServiceImpl;

  @GetMapping("/{drawId}")
  public TicketResponseDto testGenerate(@PathVariable Long drawId) {
    // 1. Получаем тираж из базы
    Draw draw =
        drawRepository.findById(drawId).orElseThrow(() -> new RuntimeException("Тираж не найден"));
    // пробуем сгенерить пул билетов, если тираж с предсозданными билетами
    ticketPoolService.generateTicketsPoolForDraw(draw);

    // 2. Генерируем билет
    // или получаем предсозданный билет из пула
    TicketResponseDto ticket = ticketsFactory.getGenerator(draw).generateTicket();


    // 3. Возвращаем результат
    return ticket;
  }

//  @PostMapping
//  public ResponseEntity<TicketResponseDto> createTicketDraft(
//      @Valid @RequestBody TicketCreateDto dto) {
//    // Создаём билет
//    Draw draw =
//        drawRepository
//            .findById(dto.getDrawId())
//            .orElseThrow(() -> new RuntimeException("Тираж не найден"));
//    User user = new User(1L); // Заглушка (реальная реализация через UserService)
//    Ticket ticket = ticketMapper.toEntity(dto);
//    ticket.setDraw(draw);
//    ticket.setUser(user);
//    ticket.setData(JsonMapper.mapNumbersToJson(ticketsFactory.getGenerator(draw).generateNumbers()));
//    ticket.setStatus(Ticket.Status.INGAME);
//
//    return ResponseEntity.status(HttpStatus.CREATED)
//        .body(ticketMapper.toDto(ticketRepository.save(ticket)));
//  }

//  @GetMapping("/{drawId}/tickets")
//  public List<Long> getTicketsByDrawId(@PathVariable Long drawId) {
//    return ticketService.getTicketIdsByDrawId(drawId);
//  }
}
