package com.example.lottery.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<AppError> catchResourceNotFoundException(NotFoundException e) {
    log.error(e.getMessage(), e);
    return new ResponseEntity<>(
        new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({
    ValidationException.class,
    IllegalArgumentException.class,
    IllegalStateException.class
  })
  public ResponseEntity<AppError> handleBadRequestExceptions(RuntimeException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    if (e instanceof IllegalArgumentException) {
      log.error("Illegal Argument error: {}", e.getMessage());
    } else {
      log.warn("Bad data or state: {} - {}", e.getClass().getSimpleName(), e.getMessage());
    }
    return new ResponseEntity<>(new AppError(status.value(), e.getMessage()), status);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    log.error(errors.toString(), e);
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<AppError> catchRuntimeException(RuntimeException e) {
    log.error(e.getMessage(), e);
    return new ResponseEntity<>(
        new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
