package com.example.lottery.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "algorithmType",
    include = JsonTypeInfo.As.PROPERTY,
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = RandomUniqueRules.class, name = "RANDOM_UNIQUE_NUMBERS"),
  @JsonSubTypes.Type(value = FixedPoolRules.class, name = "FIXED_POOL")
})
public interface AlgorithmRules {}
