package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
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
        .body(ErrorResponse.of(e, request.getRequestURI()));
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
            java.time.Instant.now(),
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다",
            java.util.Map.of(),
            e.getClass().getSimpleName(),
            500
        ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e,
      HttpServletRequest request
  ) {
    // 어떤 필드가 왜 실패했는지 details에 담기
    Map<String, Object> details = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            FieldError::getDefaultMessage,
            (existing, duplicate) -> existing
        ));

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
}