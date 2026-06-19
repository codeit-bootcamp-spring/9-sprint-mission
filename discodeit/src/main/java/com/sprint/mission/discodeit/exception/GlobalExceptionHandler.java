package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException ex,
      HttpServletRequest request) {
    ErrorCode errorCode = ex.getErrorCode();
    log.warn("DiscodeitException: code={}, message={}, details={}, path={}",
        errorCode.getCode(), errorCode.getMessage(), ex.getDetails(), request.getRequestURI());

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.from(ex));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    String message = String.format("Invalid value for '%s': %s", ex.getName(), ex.getValue());

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("parameter", ex.getName());
    details.put("rejectedValue", ex.getValue());
    details.put("requiredType",
        ex.getRequiredType() == null ? null : ex.getRequiredType().getSimpleName());

    return respond(ErrorCode.INVALID_REQUEST, message, details, ex);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    Map<String, Object> details = new LinkedHashMap<>();
    ex.getMostSpecificCause();
    details.put("cause", ex.getMostSpecificCause().getMessage());

    return respond(ErrorCode.INVALID_REQUEST, "Malformed JSON request", details, ex);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    List<Map<String, Object>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> {
          Map<String, Object> item = new LinkedHashMap<>();
          item.put("field", fieldError.getField());
          item.put("message", fieldError.getDefaultMessage());
          item.put("rejectedValue", fieldError.getRejectedValue());
          return item;
        })
        .toList();

    String message = fieldErrors.isEmpty() ? "Validation failed"
        : String.valueOf(fieldErrors.get(0).get("message"));

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("errors", fieldErrors);

    return respond(ErrorCode.INVALID_REQUEST, message, details, ex);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    return respond(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage(), Map.of(), ex);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
    return respond(
        ErrorCode.AUTHENTICATION_REQUIRED,
        ErrorCode.AUTHENTICATION_REQUIRED.getMessage(),
        Map.of(),
        ex
    );
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
      HttpServletRequest request) {
    log.warn("NoResourceFoundException: path={}", request.getRequestURI());

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("path", request.getRequestURI());

    return respond(ErrorCode.RESOURCE_NOT_FOUND, "Resource not found", details, ex);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
    log.error("Unexpected error. path={} msg={}", request.getRequestURI(), ex.getMessage(), ex);

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("message", ex.getMessage());

    return respond(ErrorCode.INTERNAL_SERVER_ERROR, "Internal server error", details, ex);
  }

  private ResponseEntity<ErrorResponse> respond(
      ErrorCode errorCode,
      String message,
      Map<String, Object> details,
      Exception ex
  ) {
    ErrorResponse body = new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        message,
        details == null ? Map.of() : Map.copyOf(details),
        ex.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    return ResponseEntity.status(errorCode.getStatus()).body(body);
  }
}
