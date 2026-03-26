package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}