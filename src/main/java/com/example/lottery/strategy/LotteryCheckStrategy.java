package com.example.lottery.strategy;

import java.util.Set;

public interface LotteryCheckStrategy {
    boolean isWinning(Set<Integer> userNumbers, Set<Integer> winningNumbers);
}