package com.sprint.mission.discodeit.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
  DUPLICATE_USER( "이미 존재하는 유저입니다."),
  DUPLICATE_EMAIL( "이미 존재하는 이메일입니다."),
  DUPLICATE_USERNAME( "이미 존재하는 유저이름입니다."),
  CHANNEL_NOT_FOUND("해당 채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE("Private 채널이 업데이트할 수 없습니다."),
  MESSAGE_NOT_FOUND("해당 메세지를 찾을 수 없습니다."),
  MESSAGE_CONTENT_INVALID("메시지 내용이 올바르지 않습니다."),
  READ_STATUS_NOT_FOUND("해당 ReadStatus를 찾을 수 없습니다."),
  BINARY_CONTENT_NOT_FOUND("해당 파일 데이터를 찾을 수 없습니다."),
  BINARY_CONTENT_TYPE_INVALID("파일 형식이 올바르지 않습니다.");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

}
