package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다"),
  USER_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 값입니다"),

  // Channel
  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채널입니다"),
  CHANNEL_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Private 채널은 수정할 수 없습니다"),

  // Message
  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다"),

  // BinaryContent
  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다");

  private final HttpStatus status;
  private final String message;
}