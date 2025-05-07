package com.example.lottery.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

public class JsonMapper {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String mapNumbersToJson(List<Integer> numbers) {
    try {
      return objectMapper.writeValueAsString(Map.of("numbers", numbers));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка сериализации чисел: " + numbers, e);
    }
  }

  public static List<Integer> mapJsonToNumbers(String json) {
    try {
      Map<String, List<Integer>> data = objectMapper.readValue(json, new TypeReference<>() {});
      return data.get("numbers");
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка парсинга чисел: " + json, e);
    }
  }

  // Сериализация объекта в JSON
  public static String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка сериализации в JSON", e);
    }
  }

  // Десериализация JSON в объект нужного типа
  public static <T> T fromJson(String json, Class<T> entity) {
    try {
      return objectMapper.readValue(json, entity);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка десериализации из JSON", e);
    }
  }
}
