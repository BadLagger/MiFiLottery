package com.example.lottery.service.Impl;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.TicketGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RandomUniqueTicketGenerator implements TicketGenerator {
    private final SecureRandom random;
    private final LotteryTypeMapper lotteryTypeMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Ticket generateTicket(Draw draw) {
        // 1. Проверяем статус тиража
        if (draw.getStatus() != DrawStatus.PLANNED && draw.getStatus() != DrawStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Нельзя генерировать билеты для тиража со статусом: " + draw.getStatus()
            );
        }

        // 2. Получаем правила из типа лотереи
        AlgorithmRules rules = lotteryTypeMapper.parseRules(draw.getLotteryType().getAlgorithmRules());

        if (!(rules instanceof RandomUniqueRules randomRules)) {
            throw new IllegalArgumentException(
                    "Генератор не поддерживает правила типа: " + rules.getClass().getSimpleName()
            );
        }

        // 3. Генерируем числа
        List<Integer> numbers = generateNumbers(randomRules);
        String jsonData = convertToJson(numbers);

        // 4. Создаём билет с привязкой к тиражу
        Ticket ticket = new Ticket();
        ticket.setData(jsonData);
        ticket.setDraw(draw); // Устанавливаем связь с тиражом
        ticket.setStatus(Ticket.Status.INGAME);

        return ticket;
    }

    // Остальные методы без изменений
    @Override
    public boolean supports(AlgorithmRules rules) {
        return rules instanceof RandomUniqueRules;
    }

    private List<Integer> generateNumbers(RandomUniqueRules rules) {
        Set<Integer> uniqueNumbers = new HashSet<>();
        while (uniqueNumbers.size() < rules.getNumberCount()) {
            int num = random.nextInt(rules.getMaxNumber() - rules.getMinNumber() + 1) + rules.getMinNumber();
            uniqueNumbers.add(num);
        }
        List<Integer> result = new ArrayList<>(uniqueNumbers);
        if (rules.isSorted()) {
            Collections.sort(result);
        }
        return result;
    }

    private String convertToJson(List<Integer> numbers) {
        try {
            Map<String, List<Integer>> data = Map.of("numbers", numbers);
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка генерации JSON", e);
        }
    }
}