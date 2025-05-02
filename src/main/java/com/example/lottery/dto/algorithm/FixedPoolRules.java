package com.example.lottery.dto.algorithm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FixedPoolRules implements AlgorithmRules {
  @Min(1)
  private Integer poolSize; // Начальный размер пула

  @Min(1)
  private Integer numberCount; // Количество чисел в билете

  @Min(1)
  private Integer minNumber; // Минимальное число

  @Min(2)
  private Integer maxNumber; // Максимальное число

  private boolean sorted = true;

  public Boolean isSorted() {
    return sorted;
  };
}
