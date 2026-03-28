package com.sprint.mission.discodeit.exepction.global;

import com.sprint.mission.discodeit.dto.response.ResponseError;
import com.sprint.mission.discodeit.exepction.DoNotDuplicate;
import com.sprint.mission.discodeit.exepction.DoNotUpdatePrivateChannel;
import com.sprint.mission.discodeit.exepction.Failed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoNotDuplicate.class)
    public ResponseEntity<ResponseError> handleDoNotDuplicate(DoNotDuplicate ex) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "이미 존재합니다: ",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Failed.class, DoNotUpdatePrivateChannel.class})
    public ResponseEntity<ResponseError> handleFailedException(RuntimeException ex) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "요청 처리 실패 : ",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Forbidden.class)
    public ResponseEntity<ResponseError> handleForbidden(Forbidden ex) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "이미 존재합니다: ",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<ResponseError> handleNotFound(NotFound ex) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "찾지 못했습니다: ",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ResponseError> handleUnauthorized(Unauthorized ex) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "로그인이 필요합니다: ",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleUnknownException(Exception ex) {
        ResponseError error = new ResponseError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "예상치 못한 오류 발생: ",
                ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}