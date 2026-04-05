package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final View error;

  public GlobalExceptionHandler(View error) {
    this.error = error;
  }

  // 1. 커스텀 예외
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {

    ErrorResponse response = ErrorResponse.builder()
        .timestamp(e.getTimestamp())
        .code(e.getErrorCode().name())
        .message(e.getMessage())
        .details(e.getDetails())
        .exceptionType(e.getClass().getSimpleName())
        .status(HttpStatus.BAD_REQUEST.value())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // 2. 예상 못한 예외 (서버 에러)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {

    ErrorResponse response = ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("INTERNAL_SERVER_ERROR")
        .message(e.getMessage())
        .details(null)
        .exceptionType(e.getClass().getSimpleName())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build();

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e
  ) {

    Map<String, Object> errors = new HashMap<>();

    e.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    ErrorResponse response = ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("VALIDATION_ERROR")
        .message("유효성 검사 실패")
        .details(errors)
        .exceptionType(e.getClass().getSimpleName())
        .status(HttpStatus.BAD_REQUEST.value())
        .build();

    return ResponseEntity.badRequest().body(response);
  }
}
