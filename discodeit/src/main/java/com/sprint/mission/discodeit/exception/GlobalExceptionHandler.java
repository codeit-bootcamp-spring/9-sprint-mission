package com.sprint.mission.discodeit.error;


import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
    log.warn("사용자 예외 발생: {}", e.getMessage());
    return ResponseEntity
        .status(e.getErrorCode().getHttpStatus())
        .body(ErrorResponse.of(e));
  }

  @ExceptionHandler(MessageException.class)
  public ResponseEntity<ErrorResponse> handleMessageException(MessageException e) {
    log.warn("메시지 예외 발생: {}", e.getMessage());
    return ResponseEntity
        .status(e.getErrorCode().getHttpStatus())
        .body(ErrorResponse.of(e));
  }

  @ExceptionHandler(BinaryContentException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentException(BinaryContentException e) {
    log.warn("컨텐츠 예외 발생: {}", e.getMessage());
    return ResponseEntity
        .status(e.getErrorCode().getHttpStatus())
        .body(ErrorResponse.of(e));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {
    log.debug("검증 실패: {}", e.getMessage());

    List<String> errors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.toList());

    return ResponseEntity.badRequest()
        .body(ErrorResponse.validationFailed(errors));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
    log.error("예상치 못한 오류 발생", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.systemError());
  }
}