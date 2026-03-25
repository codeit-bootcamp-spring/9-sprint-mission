package com.sprint.mission.discodeit.exception.base;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // USER
  USER_NOT_FOUND(101, "존재하지 않는 사용자입니다."),
  DUPLICATE_USER(102, "이미 존재하는 사용자 이름입니다."),

  // CHANNEL
  CHANNEL_NOT_FOUND(201, "채널이 존재하지 않습니다."),
  PRIVATE_CHANNEL_UPDATE(202, "PRIVATE 채널은 수정할 수 없습니다."),

  // MESSAGE
  MESSAGE_NOT_FOUND(301, "메시지가 존재하지 않습니다")
  ;


  private final int code;
  private final String message;

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

}
