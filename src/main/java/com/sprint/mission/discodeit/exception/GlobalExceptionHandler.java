package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.message.MessageException;
import com.sprint.mission.discodeit.exception.user.UserException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {

    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error -> {
      details.put(error.getField(), error.getDefaultMessage());
    });

    ErrorResponse response = ErrorResponse.builder()
        .status(400)
        .exceptionType(e.getClass().getSimpleName())
        .errorCode(ErrorCode.INVALID_REQUEST.getCode())
        .message(ErrorCode.INVALID_REQUEST.getMessage())
        .timestamp(java.time.Instant.now())
        .details(details)
        .build();

    log.warn("Validation failed: {}", details);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.warn(
        "[{}] {} - details: {}",
        e.getErrorCode().getCode(),
        e.getErrorCode().getMessage(),
        e.getDetails()
    );
    ErrorResponse response = ErrorResponse.from(e);
    return ResponseEntity
        .status(response.getStatus())
        .body(response);
  }

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
    log.warn(
        "[USER] {} - details: {}",
        e.getErrorCode().getMessage(),
        e.getDetails()
    );
    ErrorResponse response = ErrorResponse.from(e);
    return ResponseEntity
        .status(response.getStatus())
        .body(response);
  }

  @ExceptionHandler(ChannelException.class)
  public ResponseEntity<ErrorResponse> handleChannelException(ChannelException e) {
    log.warn(
        "[CHANNEL] {} - details: {}",
        e.getErrorCode().getMessage(),
        e.getDetails()
    );
    ErrorResponse response = ErrorResponse.from(e);
    return ResponseEntity
        .status(response.getStatus())
        .body(response);
  }

  @ExceptionHandler(MessageException.class)
  public ResponseEntity<ErrorResponse> handleMessageException(MessageException e) {
    log.warn(
        "[MESSAGE] {} - details: {}",
        e.getErrorCode().getMessage(),
        e.getDetails()
    );
    ErrorResponse response = ErrorResponse.from(e);
    return ResponseEntity
        .status(response.getStatus())
        .body(response);
  }

  @ExceptionHandler(BinaryContentException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentException(BinaryContentException e) {
    log.warn(
        "[BINARY_CONTENT] {} - details: {}",
        e.getErrorCode().getMessage(),
        e.getDetails()
    );
    ErrorResponse response = ErrorResponse.from(e);
    return ResponseEntity
        .status(response.getStatus())
        .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
    log.error("Unexpected exception occurred", e);
    ErrorResponse response = ErrorResponse.builder()
        .status(500)
        .exceptionType(e.getClass().getSimpleName())
        .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
        .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
        .timestamp(java.time.Instant.now())
        .build();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException e) {
    ErrorResponse response = ErrorResponse.builder()
        .status(404)
        .exceptionType(e.getClass().getSimpleName())
        .errorCode("NOT_FOUND")
        .message("요청한 리소스를 찾을 수 없습니다")
        .timestamp(java.time.Instant.now())
        .build();

    return ResponseEntity.status(404).body(response);
  }
}
