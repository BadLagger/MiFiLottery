package com.example.lottery.entity;

import java.util.Map;

public enum AlgorithmType {
  RANDOM_UNIQUE_NUMBERS,
  FIXED_POOL,
  USER_SELECTED;

  private static final Map<String, AlgorithmType> CLASS_NAME_MAPPING =
      Map.of(
          "RandomUniqueRules", RANDOM_UNIQUE_NUMBERS,
          "FixedPoolRules", FIXED_POOL,
          "UserSelectedRules", USER_SELECTED);

  public static AlgorithmType fromClassName(String className) {
    AlgorithmType type = CLASS_NAME_MAPPING.get(className);
    if (type == null) {
      throw new IllegalArgumentException(
          "Неизвестный алгоритм: "
              + className
              + ". Поддерживаемые классы: "
              + CLASS_NAME_MAPPING.keySet());
    }
    return type;
  }
}
