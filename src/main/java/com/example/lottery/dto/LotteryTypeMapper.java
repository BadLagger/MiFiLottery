package com.example.lottery.dto;

import com.example.lottery.entity.AlgorithmType;
import com.example.lottery.entity.LotteryType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class LotteryTypeMapper {
  @Autowired protected ObjectMapper objectMapper;

  // Преобразование Entity -> DTO
  @Mapping(target = "algorithmRules", expression = "java(parseRules(entity.getAlgorithmRules()))")
  public abstract LotteryTypeResponseDto toDto(LotteryType entity);

  // Преобразование DTO -> Entity
  @Mapping(
      target = "algorithmRules",
      expression = "java(convertRulesToJson(dto.getAlgorithmRules()))")
  @Mapping(
      target = "algorithmType",
      expression = "java(getAlgorithmTypeEnum(dto.getAlgorithmRules()))")
  public abstract LotteryType toEntity(LotteryTypeCreateDto dto);

  // Парсинг JSON -> Объект правил
  protected AlgorithmRules parseRules(String json) {
    try {
      log.debug("Parsing JSON: {}", json);
      return objectMapper.readValue(json, AlgorithmRules.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Не могу получить rules из JSON: " + json, e);
    }
  }

  // Конвертация Объекта правил -> JSON
  protected String convertRulesToJson(AlgorithmRules rules) {
    try {
      return objectMapper.writeValueAsString(rules);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Не могу сериализовать rules: " + rules, e);
    }
  }

  // Определение типа алгоритма
  protected AlgorithmType getAlgorithmTypeEnum(AlgorithmRules rules) {
    String className = rules.getClass().getSimpleName();
    return AlgorithmType.fromClassName(className);
  }
}
