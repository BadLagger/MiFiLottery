package com.example.lottery.controller;

import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.dto.LotteryTypeCreateDto;
import com.example.lottery.dto.LotteryTypeResponseDto;
import com.example.lottery.dto.algorithm.RandomUniqueRules;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.repository.LotteryTypeRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/lottery")
@RequiredArgsConstructor
public class LotteryTypeController {
  private final LotteryTypeMapper mapper;
  private final LotteryTypeRepository repository;

  @PostMapping
  public ResponseEntity<LotteryTypeResponseDto> createLotteryType() {
    LotteryTypeCreateDto dto = new LotteryTypeCreateDto();
    dto.setTicketPrice(new BigDecimal("150.00"));
    dto.setPrizePoolPercentage(0.6);
    dto.setMinTicket(3);
        dto.setDescription("50 наборов чисел по 4 от 10 до 30");
        FixedPoolRules rules = new FixedPoolRules();
        rules.setNumberCount(4);
        rules.setMinNumber(10);
        rules.setMaxNumber(30);
        rules.setPoolSize(50);
        dto.setAlgorithmRules(rules);

//    RandomUniqueRules rules = new RandomUniqueRules();
//    dto.setDescription("5 из 36 автогенерация");
//        rules.setNumberCount(5);
//        rules.setMinNumber(1);
//        rules.setMaxNumber(36);
//        rules.setSorted(true);
//        dto.setAlgorithmRules(rules);

    LotteryType entity = mapper.toEntity(dto);
    entity = repository.save(entity); // Сохраняем в БД

    return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LotteryTypeResponseDto> getLotteryType(@PathVariable Long id) {
    return ResponseEntity.ok(mapper.toDto(repository.getReferenceById(id)));
  }
}
