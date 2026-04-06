package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  INVALID_PASSWORD(401, "비밀번호가 일치하지 않습니다."),
  BINARY_CONTENT_NOT_FOUND(404, "존재하지 않는 파일입니다."),
  CHANNEL_NOT_FOUND(404, "존재하지 않는 채널입니다."),
  PRIVATE_CHANNEL_UPDATE(403, "Private 채널은 수정할 수 없습니다."),
  MESSAGE_NOT_FOUND(404, "존재하지 않는 메시지입니다."),
  USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
  DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
  DUPLICATE_USER(400, "이미 존재하는 사용자입니다.");

  private final int status;
  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
