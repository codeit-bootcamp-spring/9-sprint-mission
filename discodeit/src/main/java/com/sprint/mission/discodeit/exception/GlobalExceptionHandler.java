package com.sprint.mission.discodeit.exception;

import static com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND;
import static com.sprint.mission.discodeit.exception.ErrorCode.PRIVATE_CHANNEL_UPDATE;
import static com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse(e, INTERNAL_SERVER_ERROR.value());
    return ResponseEntity
        .status(INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
    log.error("커스텀 예외 발생: code={}, message={}", exception.getErrorCode(), exception.getMessage(), exception);
    HttpStatus status = determineHttpStatus(exception);
    ErrorResponse response = new ErrorResponse(exception, status.value());
    return ResponseEntity
        .status(status)
        .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    log.error("요청 유효성 검사 실패: {}", e.getMessage());

    Map<String, Object> validationErrors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
    });

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "VALIDATION_ERROR",
        "요청 데이터 유효성 검사에 실패했습니다",
        validationErrors,
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.error("권한 없음: {}", ex.getMessage());
    ErrorResponse response = new ErrorResponse(ex, HttpStatus.FORBIDDEN.value());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(response);
  }

  private HttpStatus determineHttpStatus(DiscodeitException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    return switch (errorCode) {
      case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND, BINARY_CONTENT_NOT_FOUND,
           READ_STATUS_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case DUPLICATE_USER, DUPLICATE_USERNAME, DUPLICATE_EMAIL -> HttpStatus.CONFLICT;
      case PRIVATE_CHANNEL_UPDATE, MESSAGE_CONTENT_INVALID, BINARY_CONTENT_TYPE_INVALID-> HttpStatus.BAD_REQUEST;
    };
  }
}
