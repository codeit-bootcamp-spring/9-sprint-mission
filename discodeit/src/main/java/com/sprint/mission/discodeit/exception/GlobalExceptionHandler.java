package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.warn("비즈니스 예외 발생 [{}]: {}", e.getClass().getSimpleName(), e.getMessage());

    ErrorCode errorCode = e.getErrorCode();

    ErrorResponse response = ErrorResponse.of(
        errorCode.getCode(),
        e.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        errorCode.getStatus()
    );

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
    log.warn("유효성 검사 예외 발생 [{}]: {}", e.getClass().getSimpleName(), e.getMessage());

    // 어떤 필드에서 유효성 검사가 실패했는지 상세 정보 추출
    Map<String, Object> details = new HashMap<>();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    ErrorCode errorCode = ErrorCode.INVALID_INPUT;

    ErrorResponse response = ErrorResponse.of(
        errorCode.getCode(),
        "입력값이 올바르지 않습니다.",
        details,
        e.getClass().getSimpleName(),
        errorCode.getStatus()
    );

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("잘못된 입력값 예외 발생: {}", e.getMessage());

    ErrorCode errorCode = ErrorCode.INVALID_INPUT;

    ErrorResponse response = ErrorResponse.of(
        errorCode.getCode(),
        e.getMessage(),
        null,
        e.getClass().getSimpleName(),
        errorCode.getStatus()
    );

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnhandledException(Exception e) {
    log.error("예상치 못한 서버 오류 발생: {}", e.getMessage(), e);

    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    ErrorResponse response = ErrorResponse.of(
        errorCode.getCode(),
        errorCode.getMessage(),
        null,
        e.getClass().getSimpleName(),
        errorCode.getStatus()
    );

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(response);
  }
}