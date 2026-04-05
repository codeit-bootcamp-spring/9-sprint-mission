package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 애플리케이션 내에서 의도적으로 발생시킨 비즈니스 예외(DiscodeitException) 처리
   */
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    ErrorCode errorCode = e.getErrorCode();
    
    ErrorResponse errorResponse = new ErrorResponse(
        e.getTimestamp(),
        errorCode.name(),
        errorCode.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(errorResponse);
  }

  /**
   * Spring Validation 검증 실패 시 발생하는 예외(MethodArgumentNotValidException) 처리
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    Map<String, Object> validationDetails = new HashMap<>();
    
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
        validationDetails.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        ErrorCode.INVALID_INPUT_VALUE.name(),
        ErrorCode.INVALID_INPUT_VALUE.getMessage(),
        validationDetails,
        e.getClass().getSimpleName(),
        ErrorCode.INVALID_INPUT_VALUE.getStatus().value()
    );

    return ResponseEntity
        .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
        .body(errorResponse);
  }

  /**
   * 예상하지 못한 그 외의 서버 예외(Exception) 처리
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    e.printStackTrace();
    
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        ErrorCode.INTERNAL_SERVER_ERROR.name(),
        ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
        Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error"),
        e.getClass().getSimpleName(),
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    );

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  /**
   * 필수 요청 파라미터(@RequestParam) 누락 시 발생하는 예외 처리
   */
  @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParams(org.springframework.web.bind.MissingServletRequestParameterException e) {
    Map<String, Object> details = new HashMap<>();
    details.put(e.getParameterName(), "필수 파라미터가 누락되었습니다.");

    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        ErrorCode.INVALID_INPUT_VALUE.name(),
        ErrorCode.INVALID_INPUT_VALUE.getMessage(),
        details,
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }
}
