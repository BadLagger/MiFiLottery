package com.example.lottery.mapper;

import com.example.lottery.dto.LotteryTypeCreateDto;
import com.example.lottery.dto.LotteryTypeResponseDto;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.entity.AlgorithmType;
import com.example.lottery.entity.LotteryType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

  protected String convertRulesToJson(AlgorithmRules rules) {
    try {
      ObjectNode root = objectMapper.createObjectNode();
      root.putPOJO("algorithmRules", rules);
      return objectMapper.writeValueAsString(root);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка сериализации правил", e);
    }
  }

  public AlgorithmRules parseRules(String json) {
    try {
      JsonNode root = objectMapper.readTree(json);
      return objectMapper.treeToValue(root.get("algorithmRules"), AlgorithmRules.class);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка парсинга правил", e);
    }
  }

  // Определение типа алгоритма
  public AlgorithmType getAlgorithmTypeEnum(AlgorithmRules rules) {
    String className = rules.getClass().getSimpleName();
    return AlgorithmType.fromClassName(className);
  }
}
