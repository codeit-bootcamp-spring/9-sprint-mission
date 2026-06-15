package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.secure.JwtAuthenticationException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException e) {
    log.debug("정적 리소스 없음: {}", e.getMessage());
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(JwtAuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleJwtAuthenticationException(
      JwtAuthenticationException e) {
    ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.UNAUTHORIZED.value());
    log.warn("JWT 인증 실패: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
    log.error("커스텀 예외 발생: code={}, message={}", exception.getErrorCode(), exception.getMessage(),
        exception);
    HttpStatus status = determineHttpStatus(exception);
    ErrorResponse response = new ErrorResponse(exception, status.value());
    return ResponseEntity
        .status(status)
        .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.error("요청 유효성 검사 실패: {}", ex.getMessage());

    Map<String, Object> validationErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
    });

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "VALIDATION_ERROR",
        "요청 데이터 유효성 검사에 실패했습니다",
        validationErrors,
        ex.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // Spring Security의 접근 거부 예외 처리
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "NO_AUTHORITY",
        "적절한 권한이 없습니다.",
        new HashMap<>(),
        ex.getClass().getSimpleName(),
        HttpStatus.FORBIDDEN.value()
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  private HttpStatus determineHttpStatus(DiscodeitException exception) {
    ErrorCode errorCode = exception.getErrorCode();

    return switch (errorCode) {
      case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND, BINARY_CONTENT_NOT_FOUND,
           READ_STATUS_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case DUPLICATE_USER, DUPLICATE_READ_STATUS -> HttpStatus.CONFLICT;
      case INVALID_USER_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
      case PRIVATE_CHANNEL_UPDATE, INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
      case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
      case NO_AUTHORITY -> HttpStatus.FORBIDDEN;
      case NOTIFICATION_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case AUTHORIZATION_DENIED -> HttpStatus.FORBIDDEN;
    };
  }
}
