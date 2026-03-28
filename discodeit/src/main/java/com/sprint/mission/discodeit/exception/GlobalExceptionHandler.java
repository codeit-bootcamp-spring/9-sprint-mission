package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {

        int status = e.getErrorCode().getStatus(); // 에러 코드 생성

        ErrorResponse response = ErrorResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().getCode())
            .message(e.getErrorCode().getMessage())
            .details(e.getDetails() == null ? Map.of() : e.getDetails()) // details null 방어
            .exceptionType(e.getClass().getSimpleName())
            .status(status)
            .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
            .exceptionType(e.getClass().getSimpleName())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {

        Map<String, Object> details = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(ErrorCode.INVALID_REQUEST.getCode())
            .message("유효성 검증에 실패했습니다.")
            .details(details)
            .exceptionType(e.getClass().getSimpleName())
            .status(400)
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
        MissingServletRequestParameterException e) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(ErrorCode.INVALID_REQUEST.getCode())
            .message("필수 요청 파라미터가 누락되었습니다: " + e.getParameterName())
            .details(Map.of("parameter", e.getParameterName()))
            .exceptionType(e.getClass().getSimpleName())
            .status(400)
            .build();

        return ResponseEntity.badRequest().body(response);
    }
}