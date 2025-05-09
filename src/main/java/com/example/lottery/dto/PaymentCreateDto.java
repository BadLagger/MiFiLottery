package com.example.lottery.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCreateDto {
  @NotNull
  private Long invoiceId;
  @NotNull
  @Size(min = 8, max = 20, message = "Номер карты не может быть менее 8 цифр")
  @Digits(fraction = 0, integer = 20, message = "Номер карты должен состоять только из цифр")
  private String cardNumber;
  @NotNull
  @Size(min = 3, max = 3, message = "CVC должен состоять из 3 цифр")
  @Digits(fraction = 0, integer = 3, message = "CVC должен состоять только из цифр")
  private String cvc;
}
