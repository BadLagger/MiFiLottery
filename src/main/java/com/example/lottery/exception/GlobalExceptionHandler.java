package com.example.lottery.exception;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  public ResponseEntity<Map<String, String>> handleValidationExceptions(
//      MethodArgumentNotValidException e) {
//    Map<String, String> errors = new HashMap<>();
//    e.getBindingResult()
//        .getAllErrors()
//        .forEach(
//            (error) -> {
//              String fieldName = ((FieldError) error).getField();
//              String errorMessage = error.getDefaultMessage();
//              errors.put(fieldName, errorMessage);
//            });
//    log.error(errors.toString(), e);
//    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//  }

//  @ExceptionHandler(RuntimeException.class)
//  public ResponseEntity<AppError> catchRuntimeException(RuntimeException e) {
//    log.error(e.getMessage(), e);
//    return new ResponseEntity<>(
//        new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
//        HttpStatus.INTERNAL_SERVER_ERROR);
//  }
//
//  @ExceptionHandler(ConstraintViolationException.class)
//  public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
//    Map<String, String> errors = new HashMap<>();
//    e.getConstraintViolations().forEach(v -> {
//      String field = v.getPropertyPath().toString();
//      String message = v.getMessage();
//      errors.put(field, message);
//    });
//    log.error(errors.toString(), e);
//    return ResponseEntity.badRequest().body(errors);
//  }

//
//  @ExceptionHandler(BadCredentialsException.class)
//  public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
//    return errorResponseWithStatus(
//            "Error: wrong login or password!",
//            HttpStatus.UNAUTHORIZED);
//  }

//  @ExceptionHandler(AccessDeniedException.class)
//  public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
//    return errorResponseWithStatus(
//            "Error: don't have enough permissions",
//            HttpStatus.UNAUTHORIZED);
//  }

//  @ExceptionHandler(SessionAuthenticationException.class)
//  public ResponseEntity<String> handleSessionAuthenticationException(SessionAuthenticationException ex) {
//    return errorResponseWithStatus(
//            "Error: This session already exists",
//            HttpStatus.FORBIDDEN);
//  }
//
//  @ExceptionHandler(UsernameNotFoundException.class)
//  public ResponseEntity<String> usernameNotFound(UsernameNotFoundException ex) {
//    return errorResponseWithStatus(
//            ex.getMessage(),
//            HttpStatus.NOT_FOUND);
//  }

  private ResponseEntity<String> errorResponseWithStatus(String msg, HttpStatus status) {
    log.error(msg);
    return ResponseEntity.status(status).body(msg);
  }


}
