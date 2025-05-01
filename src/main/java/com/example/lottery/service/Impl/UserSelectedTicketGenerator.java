package com.example.lottery.service.Impl;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.TicketGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSelectedTicketGenerator implements TicketGenerator {
    private final LotteryTypeMapper lotteryTypeMapper;

    @Override
    public Ticket generateTicket(Draw draw) {
        // Для этого типа лотерей билет создаётся на основе выбора пользователя,
        // поэтому этот метод не используется напрямую.
        throw new UnsupportedOperationException("Для UserSelected используется handler создания билета с числами от пользователя");
    }

    @Override
    public boolean supports(AlgorithmRules rules) {
        return rules instanceof RandomUniqueRules; // TODO: отдельный тип UserSelectedRules
    }

    // Дополнительный метод для валидации выбранных чисел
    public void validateUserNumbers(List<Integer> numbers, LotteryType lotteryType) {
        AlgorithmRules rules = lotteryTypeMapper.parseRules(lotteryType.getAlgorithmRules());
        if (rules instanceof RandomUniqueRules randomRules) {
            // Проверка количества, диапазона, уникальности и сортировки
            // (аналогично валидации в TicketServiceImpl)
        }
    }
}
