package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
}