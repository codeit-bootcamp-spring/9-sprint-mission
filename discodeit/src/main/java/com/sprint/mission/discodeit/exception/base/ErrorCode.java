package com.sprint.mission.discodeit.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // SYSTEM
  INVALID_INPUT_VALUE(1001, "입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

  // USER
  USER_NOT_FOUND(2001, "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_USER(2002, "이미 존재하는 사용자 입니다.", HttpStatus.CONFLICT),

  // CHANNEL
  CHANNEL_NOT_FOUND(3001, "채널이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  PRIVATE_CHANNEL_UPDATE(3002, "PRIVATE 채널은 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),

  // MESSAGE
  MESSAGE_NOT_FOUND(4001, "메시지가 존재하지 않습니다", HttpStatus.NOT_FOUND)
  ;


  private final int code;
  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(int code, String message, HttpStatus httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }

}
