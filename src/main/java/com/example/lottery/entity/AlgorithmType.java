package com.example.lottery.entity;

import com.example.lottery.dto.DrawStatus;
import java.util.Map;
import java.util.Set;

public enum AlgorithmType {
  // Типы алгоритмов
  // Каждый тип алгоритма имеет набор разрешенных статусов генерации/продажи билетов
  RANDOM_UNIQUE_NUMBERS(Set.of(DrawStatus.ACTIVE)),
  FIXED_POOL(Set.of(DrawStatus.PLANNED, DrawStatus.ACTIVE)),
  USER_SELECTED(Set.of(DrawStatus.ACTIVE));

  private final Set<DrawStatus> allowedDrawStatuses;

  // Конструктор для инициализации доступных статусов
  AlgorithmType(Set<DrawStatus> allowedDrawStatuses) {
    this.allowedDrawStatuses = allowedDrawStatuses;
  }

  // Метод для проверки, разрешен ли данный статус для этого типа алгоритма
  public boolean isDrawStatusAllowed(DrawStatus drawStatus) {
    return allowedDrawStatuses.contains(drawStatus);
  }

  // Статическое отображение для связи классов правил с типами алгоритмов
  private static final Map<String, AlgorithmType> CLASS_NAME_MAPPING =
          Map.of(
                  "RandomUniqueRules", RANDOM_UNIQUE_NUMBERS,
                  "FixedPoolRules", FIXED_POOL,
                  "UserSelectedRules", USER_SELECTED);

  // Метод для получения типа алгоритма по имени класса правил
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