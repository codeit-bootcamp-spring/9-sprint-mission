package com.sprint.mission.discodeit.exception.base;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // SYSTEM
  INVALID_INPUT_VALUE(1001, "입력값이 유효하지 않습니다."),

  // USER
  USER_NOT_FOUND(2001, "존재하지 않는 사용자입니다."),
  DUPLICATE_USER(2002, "이미 존재하는 사용자 입니다."),

  // CHANNEL
  CHANNEL_NOT_FOUND(3001, "채널이 존재하지 않습니다."),
  PRIVATE_CHANNEL_UPDATE(3002, "PRIVATE 채널은 수정할 수 없습니다."),

  // MESSAGE
  MESSAGE_NOT_FOUND(4001, "메시지가 존재하지 않습니다")
  ;


  private final int code;
  private final String message;

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

}
