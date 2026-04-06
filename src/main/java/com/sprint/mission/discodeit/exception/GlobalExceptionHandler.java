package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(
      DiscodeitException e,
      HttpServletRequest request
  ) {
    log.warn("DiscodeitException 발생: code={}, path={}",
        e.getErrorCode().name(), request.getRequestURI());
    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(new ErrorResponse(e, e.getErrorCode().getStatus().value())); // ← of() 대신 생성자 사용
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e,
      HttpServletRequest request
  ) {
    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      details.putIfAbsent(fieldName, errorMessage);
    });

    log.warn("유효성 검증 실패: path={}, details={}", request.getRequestURI(), details);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
            Instant.now(),
            "VALIDATION_ERROR",
            "유효성 검증에 실패했습니다",
            details,
            e.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(
      Exception e,
      HttpServletRequest request
  ) {
    log.error("처리되지 않은 예외 발생: path={}, message={}",
        request.getRequestURI(), e.getMessage());
    return ResponseEntity
        .status(500)
        .body(new ErrorResponse(
            Instant.now(),
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다",
            Map.of(),
            e.getClass().getSimpleName(),
            500
        ));
  }
}

