package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.warn("Business Exception: {} - {}", errorCode.getCode(), e.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(
        e.getTimestamp(), errorCode.getCode(), e.getMessage(), e.getDetails(),
        e.getClass().getSimpleName(), errorCode.getStatus().value()
    ));
  }


  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e) {
    return ResponseEntity.status(e.getStatusCode()).body(new ErrorResponse(
        Instant.now(), "G002", e.getReason(), null,
        e.getClass().getSimpleName(), e.getStatusCode().value()
    ));
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    String errors = e.getBindingResult().getFieldErrors().stream()
        .map(f -> f.getField() + ": " + f.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(new ErrorResponse(
        Instant.now(), "V001", "Validation failed: " + errors, null,
        e.getClass().getSimpleName(), 400
    ));
  }


  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(
        Instant.now(), "F002", "파일 용량이 너무 큽니다. (최대 10MB)", null,
        e.getClass().getSimpleName(), 400
    ));
  }


  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException e) {
    return ResponseEntity.status(405).body(new ErrorResponse(
        Instant.now(), "G001", "지원하지 않는 메서드입니다.", null,
        e.getClass().getSimpleName(), 405
    ));
  }


  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(Exception e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(
        Instant.now(), "V002", "데이터 형식이 올바르지 않습니다.", null,
        e.getClass().getSimpleName(), 400
    ));
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception e) {
    log.error("Internal Server Error: ", e);
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(500).body(new ErrorResponse(
        Instant.now(), errorCode.getCode(), errorCode.getMessage(), null,
        e.getClass().getSimpleName(), 500
    ));
  }

  @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParams(
      MissingServletRequestParameterException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(
        Instant.now(), "V003", "필수 파라미터가 누락되었습니다: " + e.getParameterName(), null,
        e.getClass().getSimpleName(), 400
    ));
  }
}