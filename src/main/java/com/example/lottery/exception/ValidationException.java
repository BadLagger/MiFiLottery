package com.example.lottery.exception;

public class ValidationException extends RuntimeException {
  public ValidationException(String message, Object... args) {
    super(String.format(message, args));
  }
}
