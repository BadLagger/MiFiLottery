package com.example.lottery.strategy;

import org.springframework.stereotype.Component;

import java.util.Set;

/*
Стратегия для простого типа лотереи
 */
@Component("simple-match")
public class SimpleMatchStrategy implements LotteryCheckStrategy {
    @Override
    public boolean isWinning(Set<Integer> userNumbers, Set<Integer> winningNumbers) {
        // Для простого типа лотереи просто проверяем совпадение с выигрышным билетом
        return userNumbers.equals(winningNumbers);
    }
}