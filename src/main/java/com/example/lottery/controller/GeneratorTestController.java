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
//  private final TicketService ticketService;

  @GetMapping("/{drawId}")
  public TicketResponseDto testGenerate(@PathVariable Long drawId) {
    // 1. Получаем тираж из базы
    Draw draw =
        drawRepository.findById(drawId).orElseThrow(() -> new RuntimeException("Тираж не найден"));
    //        AlgorithmRules rules =
    // lotteryTypeMapper.parseRules(draw.getLotteryType().getAlgorithmRules());

    // 2. Генерируем билет
    //        Ticket ticket = ticketMaker.create(draw, uniqueNumbersGenerator.generateNumbers(rules,
    // draw));

    // получаем предсозданный билет из пула
    TicketResponseDto ticket = ticketsFactory.getGenerator(draw).generateTicket();

    // пробуем сгенерить пул билетов
    //        drawService.initPoolForDraw(draw);

    // 3. Возвращаем результат
    return ticket;
    //        return String.format(
    //                "Тест генератора:<br>" +
    //                        "Тираж: %s<br>" +
    //                        "Тип лотереи: %s<br>" +
    //                        "Сгенерированный билет: %s c ID %d",
    //                draw.getName(),
    //                draw.getLotteryType().getDescription(),
    //                ticket.getData(),
    //                ticket.getId()
    //        );
  }

  @PostMapping
  public ResponseEntity<TicketResponseDto> createTicketDraft(
      @Valid @RequestBody TicketCreateDto dto) {
    // Создаём билет
    Draw draw =
        drawRepository
            .findById(dto.getDrawId())
            .orElseThrow(() -> new RuntimeException("Тираж не найден"));
    User user = new User(1L); // Заглушка (реальная реализация через UserService)
    Ticket ticket = ticketMapper.toEntity(dto);
    ticket.setDraw(draw);
    ticket.setUser(user);
    ticket.setData(JsonMapper.mapNumbersToJson(ticketsFactory.getGenerator(draw).generateNumbers()));
    ticket.setStatus(Ticket.Status.INGAME);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ticketMapper.toDto(ticketRepository.save(ticket)));
  }
//
//  @GetMapping("/{drawId}/tickets")
//  public List<Long> getTicketsByDrawId(@PathVariable Long drawId) {
//    return ticketService.getTicketIdsByDrawId(drawId);
//  }
}
