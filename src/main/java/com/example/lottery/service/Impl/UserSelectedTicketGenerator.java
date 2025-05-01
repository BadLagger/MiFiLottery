package com.example.lottery.service.Impl;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.service.AbstractTicketGenerator;
import com.example.lottery.service.TicketGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSelectedTicketGenerator extends AbstractTicketGenerator {
    private final LotteryTypeMapper lotteryTypeMapper;

    @Override
    public Ticket generateTicket() {
        // TODO: Для этого типа лотерей билет создаётся на основе выбора пользователя,
        // поэтому этот метод не используется напрямую.
        throw new UnsupportedOperationException("Для UserSelected используется handler создания билета с числами от пользователя");
    }

    @Override
    public boolean supports(AlgorithmRules rules) {
        return rules instanceof RandomUniqueRules;
    }
}
