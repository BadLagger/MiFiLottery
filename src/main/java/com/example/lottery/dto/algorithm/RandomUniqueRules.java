package com.example.lottery.dto.algorithm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomUniqueRules implements AlgorithmRules {
  @Min(1)
  @Max(100)
  private int numberCount;

  @Min(1)
  @Max(1000)
  private int minNumber;

  @Min(2)
  @Max(1000)
  private int maxNumber;

  private boolean sorted;
  private boolean allowDuplicates;
}
