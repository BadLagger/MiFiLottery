package com.example.lottery.dto.algorithm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "algorithmType",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = RandomUniqueRules.class, name = "RANDOM_UNIQUE_NUMBERS"),
  @JsonSubTypes.Type(value = FixedPoolRules.class, name = "FIXED_POOL"),
  @JsonSubTypes.Type(value = UserSelectedRules.class, name = "USER_SELECTED")
})
public interface AlgorithmRules {
  Integer getNumberCount();

  Integer getMaxNumber();

  Integer getMinNumber();
}
