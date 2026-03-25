package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            "서버 내부 오류가 발생했습니다 - " + e.getMessage(),
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
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }
    // User
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    // Channel
    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChannelNotFoundException(ChannelNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateException(PrivateChannelUpdateException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }
    
    // Message
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChannelNotFoundException(MessageNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse response = ErrorResponse.of(e, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }
}
