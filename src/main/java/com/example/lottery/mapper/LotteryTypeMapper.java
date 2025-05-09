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
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LotteryTypeMapper {

  // Преобразование Entity -> DTO
  @Mapping(target = "algorithmRules", expression = "java(parseRules(entity.getAlgorithmRules()))")
  LotteryTypeResponseDto toDto(LotteryType entity);

  // Преобразование DTO -> Entity
  @Mapping(
      target = "algorithmRules",
      expression = "java(convertRulesToJson(dto.getAlgorithmRules()))")
  @Mapping(
      target = "algorithmType",
      expression = "java(getAlgorithmTypeEnum(dto.getAlgorithmRules()))")
  LotteryType toEntity(LotteryTypeCreateDto dto);

  default String convertRulesToJson(AlgorithmRules rules) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      ObjectNode root = objectMapper.createObjectNode();
      root.putPOJO("algorithmRules", rules);
      return objectMapper.writeValueAsString(root);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка сериализации правил", e);
    }
  }

  default AlgorithmRules parseRules(String json) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode root = objectMapper.readTree(json);
      return objectMapper.treeToValue(root.get("algorithmRules"), AlgorithmRules.class);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка парсинга правил", e);
    }
  }

  // Определение типа алгоритма
  default AlgorithmType getAlgorithmTypeEnum(AlgorithmRules rules) {
    String className = rules.getClass().getSimpleName();
    return AlgorithmType.fromClassName(className);
  }
}
