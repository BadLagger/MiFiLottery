package com.example.lottery.strategy;

import com.example.lottery.entity.LotteryType;
import com.example.lottery.repository.LotteryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LotteryCheckStrategyResolver {

    private final Map<String, LotteryCheckStrategy> strategyMap;
    private final LotteryTypeRepository lotteryTypeRepository;

    @Autowired
    public LotteryCheckStrategyResolver(Map<String, LotteryCheckStrategy> strategyMap, LotteryTypeRepository lotteryTypeRepository) {
        this.strategyMap = strategyMap;
        this.lotteryTypeRepository = lotteryTypeRepository;
    }

    public LotteryCheckStrategy resolve(Long lotteryTypeId) {
        // Получаем тип лотереи по ID из базы данных
        LotteryType lotteryType = lotteryTypeRepository.findById(lotteryTypeId)
                .orElseThrow(() -> new RuntimeException("LotteryType not found"));

        // Дальше можно использовать описание типа лотереи или ID для выбора нужной стратегии
        if ("SIMPLE".equals(lotteryType.getDescription())) {
            return strategyMap.get("simple-match");
        } else if ("COMPLEX".equals(lotteryType.getDescription())) {
            return strategyMap.get("exact-match");
        }

        throw new RuntimeException("No strategy found for this lottery type");
    }
}
