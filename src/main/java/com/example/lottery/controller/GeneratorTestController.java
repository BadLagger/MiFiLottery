package com.example.lottery.controller;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.service.DrawService;
import com.example.lottery.service.Impl.RandomUniqueTicketGenerator;
import com.example.lottery.service.TicketGenerator;
import com.example.lottery.service.TicketsFactory;
import com.example.lottery.service.utils.TicketMaker;
import com.example.lottery.service.utils.UniqueNumbersGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/generator")
@RequiredArgsConstructor
public class GeneratorTestController {
    private final UniqueNumbersGenerator uniqueNumbersGenerator;
    private final TicketMaker ticketMaker;
    private final DrawRepository drawRepository;
    private final DrawService drawService;
    private final LotteryTypeMapper lotteryTypeMapper;
    private final TicketsFactory ticketsFactory;

    @GetMapping("/{drawId}")
    public Ticket testGenerate(@PathVariable Long drawId) {
        // 1. Получаем тираж из базы
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new RuntimeException("Draw not found"));
        AlgorithmRules rules = lotteryTypeMapper.parseRules(draw.getLotteryType().getAlgorithmRules());

        // 2. Генерируем билет
//        Ticket ticket = ticketMaker.create(draw, uniqueNumbersGenerator.generateNumbers(rules, draw));

        // получаем предсозданный билет из пула
        Ticket ticket = ticketsFactory.getGenerator(draw).generateTicket();

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
}