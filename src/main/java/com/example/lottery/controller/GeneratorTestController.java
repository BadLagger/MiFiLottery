package com.example.lottery.controller;

import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.entity.User;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.service.TicketService;
import com.example.lottery.service.TicketsFactory;
import com.example.lottery.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test/generator")
@RequiredArgsConstructor
public class GeneratorTestController {
  private final TicketMapper ticketMapper;
  private final DrawRepository drawRepository;
  private final TicketRepository ticketRepository;
  private final TicketsFactory ticketsFactory;
  private final TicketService ticketService;
  private final UserService userService;

  private void createRealTicket(TicketResponseDto ticketResponseDto, String username, Draw draw) {

    log.debug("Try to create real ticket");

      User user = userService.getUserByName(username);

      if (user == null) {
        log.debug("Failed to create real ticket");
        return;
      }

      Ticket newTicket = new Ticket();

      newTicket.setDraw(draw);
      newTicket.setUser(user);
      newTicket.setStatus(Ticket.Status.INGAME);

      ObjectMapper objectMapper = new ObjectMapper();

      try {
         newTicket.setData(objectMapper.writeValueAsString(Map.of("numbers", ticketResponseDto.getNumbers())));
      } catch (JsonProcessingException e) {
        log.debug("Failed to create numbers");
        return;
      }
      log.debug("Real Ticket: {}", newTicket);
      ticketRepository.save(newTicket);
      log.debug("Create real ticket DONE!");
  }

  @GetMapping("/{drawId}")
  public TicketResponseDto testGenerate(@PathVariable Long drawId) {
    // 1. Получаем тираж из базы
    Draw draw =
        drawRepository.findById(drawId).orElseThrow(() -> new RuntimeException("Тираж не найден"));
    //        AlgorithmRules rules =
    // lotteryTypeMapper.parseRules(draw.getLotteryType().getAlgorithmRules());

    // Получем контекст авторизации для привязки пользователя
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // 2. Генерируем билет
    //        Ticket ticket = ticketMaker.create(draw, uniqueNumbersGenerator.generateNumbers(rules,
    // draw));

    // получаем предсозданный билет из пула
    TicketResponseDto ticket = ticketsFactory.getGenerator(draw).generateTicket();
    log.debug("Ticket DTO: {}", ticket);
    // пробуем сгенерить пул билетов
    //        drawService.initPoolForDraw(draw);


    // Пробуем создать реальный билет
    createRealTicket(ticket, auth.getName(), draw);

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
    // \todo Что именно должен возвращать UserService? Возможно в запрос необходимо добавить User ID?
   // User user = new User(1L); // Заглушка (реальная реализация через UserService)
    Ticket ticket = ticketMapper.toEntity(dto);
    ticket.setDraw(draw);
   // ticket.setUser(user);
    ticket.setStatus(Ticket.Status.INGAME);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ticketMapper.toDto(ticketRepository.save(ticket)));
  }
}
