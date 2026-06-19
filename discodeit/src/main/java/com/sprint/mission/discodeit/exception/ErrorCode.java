package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  // 유저 에러 목록
  USER_NOT_FOUND("존재하지 않는 사용자입니다."),
  DUPLICATE_USER("이미 존재하는 사용자입니다."),

  // 채널 에러 목록
  CHANNEL_NOT_FOUND("존재하지 않는 채널입니다."),
  PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다."),

  // 메시지 에러 목록
  MESSAGE_NOT_FOUND("존재하지 않는 메시지입니다."),

  // 파일(바이너리) 에러 목록
  BINARY_CONTENT_NOT_FOUND("존재하지 않는 파일입니다."),
  FILE_READ_ERROR("파일을 읽는 중 시스템 오류가 발생했습니다."),

  // 알림 에러 목록
  NOTIFICATION_NOT_FOUND("존재하지 않는 알림입니다."),
  NOTIFICATION_ACCESS_DENIED("본인의 알림만 삭제할 수 있습니다.");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}