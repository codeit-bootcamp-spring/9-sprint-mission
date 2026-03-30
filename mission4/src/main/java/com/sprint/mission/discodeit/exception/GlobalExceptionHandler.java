package com.sprint.mission.discodeit.exception;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handlerDiscodeitException(DiscodeitException e) {
    log.warn("사용자 예외 발생:{}", e.getMessage());
    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse errorResponse = new ErrorResponse(
        e.getTimeStamp(),
        errorCode.name(),
        e.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        errorCode.getStatus()
    );
    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors()
        .forEach(error -> details.put(error.getField(), error.getDefaultMessage()));
    ErrorResponse response = new ErrorResponse(
        null,
        "BAD_REQUEST",
        errorMessage,
        details,
        e.getClass().getSimpleName(),
        400

    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handlerHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    log.warn("JSON 실패(잘못된 데이터 형식) : {}", e.getMessage());
    ErrorResponse response = ErrorResponse.of(
        Instant.now(),
        "INVALID_JSON_FORMAT",
        "요청 데이터 형식이 올바르지 않습니다.",
        null,
        e.getClass().getSimpleName(),
        400
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handlerException(Exception e) {
    ErrorCode error = ErrorCode.INTERNAL_SERVER_ERROR;
    ErrorResponse errorResponse = ErrorResponse.of(
        Instant.now(),
        error.name(),
        "서버 내부 오류 발생",
        null,
        e.getClass().getSimpleName(),
        error.getStatus()
    );

    log.error("예상치 못한 에러 발생:", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }
}



