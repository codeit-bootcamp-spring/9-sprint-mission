package com.sprint.mission.discodeit.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 커스텀 예외 통합 처리 (DiscodeitException 및 그 자식들)
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.warn("커스텀 예외 발생: {}", e.getErrorCode().getMessage());

    ErrorResponse response = new ErrorResponse(
        e.getTimestamp(),
        e.getErrorCode().name(),
        e.getErrorCode().getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(), // 발생한 예외의 진짜 클래스 이름
        HttpStatus.BAD_REQUEST.value() // HTTP 상태 코드
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // 자바 기본 예외 (IllegalArgumentException) - 일관된 응답 적용
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("잘못된 요청 예외 발생: {}", e.getMessage());

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "BAD_REQUEST",
        e.getMessage(),
        new HashMap<>(), // 상세 정보가 없으므로 빈 맵 객체 할당
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // 자바 기본 예외 (NoSuchElementException) - 일관된 응답 적용
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
    log.warn("요청 데이터 없음 예외 발생: {}", e.getMessage());

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "NOT_FOUND",
        e.getMessage(),
        new HashMap<>(),
        e.getClass().getSimpleName(),
        HttpStatus.NOT_FOUND.value()
    );

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(response);
  }

  // 나머지 모든 알 수 없는 예외 처리 (Exception) - 일관된 응답 적용
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("서버 내부 오류 발생: ", e);

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "INTERNAL_SERVER_ERROR",
        e.getMessage() != null ? e.getMessage() : "서버 내부 오류",
        new HashMap<>(),
        e.getClass().getSimpleName(),
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    );

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }

  // 유효성 검사 실패 시 발생하는 에러를 낚아채는 핸들러
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {
    log.warn("유효성 검사 실패: 잘못된 입력값이 존재합니다.");

    // 어떤 변수(예: email)에서 무슨 에러(예: 이메일 형식이 아닙니다)가 났는지 details 바구니에 담습니다.
    Map<String, Object> details = new HashMap<>();
    for (org.springframework.validation.FieldError fieldError : e.getBindingResult()
        .getFieldErrors()) {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    // 일관된 ErrorResponse 객체를 생성(초기화)합니다.
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "INVALID_INPUT_VALUE",
        "입력값이 올바르지 않습니다.",
        details,
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }
}