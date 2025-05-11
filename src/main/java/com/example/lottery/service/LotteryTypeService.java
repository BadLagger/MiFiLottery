package com.example.lottery.service;

import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.AlgorithmType;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.repository.LotteryTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryTypeService {

  private final LotteryTypeRepository lotteryTypeRepository;
  private final LotteryTypeMapper lotteryTypeMapper;

  public Optional<LotteryType> findLotteryTypeById(Long id) {
    return lotteryTypeRepository.findById(id);
  }

  public LotteryType getLotteryTypeById(Long id) {
    LotteryType lotteryType =
        lotteryTypeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Тип лотереи с ID = " + id + " не найден"));
    return lotteryType;
  }

  public AlgorithmType getAlgorithmTypeByLotteryTypeId(Long id) {
      return getLotteryTypeById(id).getAlgorithmType();
  }

  public AlgorithmType getAlgorithmTypeByDrawId(Long drawId, DrawService drawService) {
      return drawService.getDrawById(drawId).getLotteryType().getAlgorithmType();
  }

  public AlgorithmType getAlgorithmTypeByDraw(Draw draw ) {
      return draw.getLotteryType().getAlgorithmType();
  }

  public AlgorithmRules getAlgorithmRulesByDrawId(Long drawId, DrawService drawService) {
      return lotteryTypeMapper.parseRules(drawService.getDrawById(drawId).getLotteryType().getAlgorithmRules());
  }

  public AlgorithmRules getAlgorithmRulesByDraw(Draw draw ) {
      return lotteryTypeMapper.parseRules(draw.getLotteryType().getAlgorithmRules());
  }
}
