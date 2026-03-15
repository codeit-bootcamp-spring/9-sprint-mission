package com.sprint.mission.discodeit.controller;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j; // 1. 임포트는 잘 되어 있습니다!

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
  public ResponseEntity<String> handleBadRequest(RuntimeException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAll(Exception e) {
    log.error("서버 내부 오류 발생", e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("서버 내부 오류가 발생했습니다: " + e.getMessage());
  }
}