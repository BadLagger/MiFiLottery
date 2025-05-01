package com.example.lottery.mapper.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapConverter {
  private final ObjectMapper objectMapper;

  public String mapNumbersToJson(List<Integer> numbers) {
    try {
      return objectMapper.writeValueAsString(Map.of("numbers", numbers));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка сериализации чисел: " + numbers, e);
    }
  }

  public List<Integer> mapJsonToNumbers(String json) {
    try {
      return objectMapper
          .readValue(json, new TypeReference<Map<String, List<Integer>>>() {})
          .get("numbers");
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка парсинга чисел: " + json, e);
    }
  }
}
