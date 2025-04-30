package com.example.lottery.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.Data;

@Data
public class FixedPoolRules implements AlgorithmRules {
  @Size(min = 1, max = 100)
  private List<@Size(min = 1, max = 100) List<@Min(1) Integer>> pools;
}
