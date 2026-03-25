package com.sprint.mission.discodeit.exception;


import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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



