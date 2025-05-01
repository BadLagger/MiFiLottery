package com.example.lottery.dto.algorithm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomUniqueRules implements AlgorithmRules {
  @Min(1)
  @Max(100)
  private Integer numberCount;

  @Min(1)
  @Max(1000)
  private Integer minNumber;

  @Min(2)
  @Max(1000)
  private Integer maxNumber;

  private boolean sorted = true;
}
