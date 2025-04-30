package com.example.lottery.controller;

import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.service.Impl.RandomUniqueTicketGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/generator")
@RequiredArgsConstructor
public class GeneratorTestController {
    private final RandomUniqueTicketGenerator generator;
    private final DrawRepository drawRepository;

    @GetMapping("/{drawId}")
    public String testGenerate(@PathVariable Long drawId) {
        // 1. Получаем тираж из базы
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new RuntimeException("Draw not found"));

        // 2. Генерируем билет
        Ticket ticket = generator.generateTicket(draw);

        // 3. Возвращаем результат
        return String.format(
                "Тест генератора:<br>" +
                        "Тираж: %s<br>" +
                        "Тип лотереи: %s<br>" +
                        "Сгенерированный билет: %s",
                draw.getName(),
                draw.getLotteryType().getDescription(),
                ticket.getData()
        );
    }
}