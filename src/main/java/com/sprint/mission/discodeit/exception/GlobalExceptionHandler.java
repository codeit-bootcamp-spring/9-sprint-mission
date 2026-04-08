package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 핸들러.
 * 모든 컨트롤러에서 발생하는 예외를 ErrorResponse 형식으로 일관되게 처리한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 비즈니스 로직 예외 처리 (도메인별 커스텀 예외) */
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        log.warn("[{}] {}: {}", e.getErrorCode(), e.getClass().getSimpleName(), e.getDetails());
        ErrorResponse response = ErrorResponse.from(e);
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(response);
    }

    /** @Valid 유효성 검사 실패 처리 (필드별 에러 메시지 포함) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException e) {
        Map<String, Object> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
            .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Validation failed: {}", fieldErrors);

        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            ErrorCode.VALIDATION_FAILED.name(),
            ErrorCode.VALIDATION_FAILED.getMessage(),
            fieldErrors,
            e.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /** 지원하지 않는 HTTP 메서드 요청 처리 (예: GET 전용 API에 POST 요청) */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
        HttpRequestMethodNotSupportedException e) {
        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            "METHOD_NOT_ALLOWED",
            e.getMessage(),
            Map.of(),
            e.getClass().getSimpleName(),
            HttpStatus.METHOD_NOT_ALLOWED.value()
        );
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(response);
    }

    /** 예상치 못한 서버 오류 처리 (500 Internal Server Error) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다",
            Map.of(),
            e.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
