package com.example.lottery.service;

import com.example.lottery.dto.LotteryTypeCreateDto;
import com.example.lottery.dto.LotteryTypeResponseDto;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.AlgorithmType;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.repository.LotteryTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    return findLotteryTypeById(id)
        .orElseThrow(() -> new NotFoundException("Тип лотереи с ID = " + id + " не найден"));
  }

  public AlgorithmType getAlgorithmTypeByLotteryTypeId(Long id) {
    return getLotteryTypeById(id).getAlgorithmType();
  }

  public AlgorithmType getAlgorithmTypeByDrawId(Long drawId, DrawService drawService) {
    return drawService.getDrawById(drawId).getLotteryType().getAlgorithmType();
  }

  public AlgorithmType getAlgorithmTypeByDraw(Draw draw) {
    return draw.getLotteryType().getAlgorithmType();
  }

  public AlgorithmRules getAlgorithmRulesByDrawId(Long drawId, DrawService drawService) {
    return lotteryTypeMapper.parseRules(
        drawService.getDrawById(drawId).getLotteryType().getAlgorithmRules());
  }

  public AlgorithmRules getAlgorithmRulesByDraw(Draw draw) {
    return lotteryTypeMapper.parseRules(draw.getLotteryType().getAlgorithmRules());
  }

  public LotteryTypeResponseDto createLotteryType(LotteryTypeCreateDto dto) {
    if (lotteryTypeRepository.existsByDescription(dto.getDescription())) {
      throw new ValidationException("Тип лотереи с таким описанием уже существует");
    }

    LotteryType entity = lotteryTypeMapper.toEntity(dto);
    LotteryType saved = lotteryTypeRepository.save(entity);
    return lotteryTypeMapper.toDto(saved);
  }

  public List<LotteryTypeResponseDto> getAll() {
    return lotteryTypeRepository.findAll().stream()
        .map(lotteryTypeMapper::toDto)
        .collect(Collectors.toList());
  }

  public void deleteById(Long id) {
    if (findLotteryTypeById(id).isPresent()) lotteryTypeRepository.deleteById(id);
  }
}
