package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.security.NotExistRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다 - ",
            null,
            e.getClass().getSimpleName(),
            status.value()
        );
        return ResponseEntity
            .status(status)
            .body(response);
    }

    // validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            details.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            ErrorCode.INVALID_INPUT_VALUE.name(),
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            details,
            e.getClass().getSimpleName(),
            status.value()
        );

        return ResponseEntity
            .status(status)
            .body(response);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            "UNAUTHORIZED",
            "로그인 오류 - 아이디/비밀번호을 다시 확인해주세요",
            null,
            e.getClass().getSimpleName(),
            status.value()
        );
        return ResponseEntity
            .status(status)
            .body(response);
    }

    // custom exception
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e){
        HttpStatus status = e.getErrorCode().getHttpStatus();
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }
    // User
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e){
        HttpStatus status = e.getErrorCode().getHttpStatus();
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        HttpStatus status = e.getErrorCode().getHttpStatus();
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    // Channel
    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChannelNotFoundException(ChannelNotFoundException e){
        HttpStatus status = e.getErrorCode().getHttpStatus();
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateException(PrivateChannelUpdateException e){
        HttpStatus status = e.getErrorCode().getHttpStatus();
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }
    
    // Message
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotFoundException(MessageNotFoundException e){
        HttpStatus status = e.getErrorCode().getHttpStatus();
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    // Security
    @ExceptionHandler(NotExistRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedTokenException(
        NotExistRefreshTokenException e){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }
}
