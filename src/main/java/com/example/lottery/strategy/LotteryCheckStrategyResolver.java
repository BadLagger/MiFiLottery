package com.example.lottery.strategy;

import com.example.lottery.entity.AlgorithmType;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.repository.LotteryTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
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
        log.debug("Try to find lottery id");
        LotteryType lotteryType = lotteryTypeRepository.findById(lotteryTypeId)
                .orElseThrow(() -> new RuntimeException("LotteryType not found"));

        log.debug("Lottery type again: {}", lotteryType);

        if (lotteryType.getAlgorithmType() == AlgorithmType.RANDOM_UNIQUE_NUMBERS) {
            log.debug("Get random strategy");
            return strategyMap.get("simple-match");
        }
        // Дальше можно использовать описание типа лотереи или ID для выбора нужной стратегии
        /*if ("SIMPLE".equals(lotteryType.getDescription())) {
            return strategyMap.get("simple-match");
        } else if ("COMPLEX".equals(lotteryType.getDescription())) {
            return strategyMap.get("exact-match");
        }*/

        throw new RuntimeException("No strategy found for this lottery type");
    }
}
