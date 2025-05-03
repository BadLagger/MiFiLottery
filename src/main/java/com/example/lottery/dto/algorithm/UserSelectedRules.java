package com.example.lottery.dto.algorithm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSelectedRules implements AlgorithmRules {
  @Min(1)
  @Max(100)
  private Integer numberCount;

  @Min(1)
  private Integer minNumber;

  @Min(2)
  private Integer maxNumber;

  private boolean sorted = true;

  public Boolean isSorted() {
    return sorted;
  };
}
