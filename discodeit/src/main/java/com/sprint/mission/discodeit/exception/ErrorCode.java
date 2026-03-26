package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode { //코드 + 메시지 일관성 있게 관리

  INTERNAL_SERVER_ERROR("D1000", "서버 내부 오류가 발생했습니다.", 500),
  INVALID_REQUEST("D1001", "잘못된 요청입니다.", 400),


  AUTH_LOGIN_FAILED("A1000", "아이디 또는 비밀번호가 올바르지 않습니다.", 401),

  USER_NOT_FOUND("U1000", "사용자를 찾을 수 없습니다.", 404),
  DUPLICATE_USER("U1001", "사용자가 이미 존재합니다.", 409),

  CHANNEL_NOT_FOUND("C1000", "채널을 찾을 수 없습니다.", 404),
  CHANNEL_ACCESS_DENIED("C1001", "채널 접근 권한이 없습니다.", 403),

  PRIVATE_CHANNEL_UPDATE("C1002", "PRIVATE 채널은 수정할 수 없습니다.", 400),

  BINARY_CONTENT_NOT_FOUND("B1000", "바이너리 콘텐츠를 찾을 수 없습니다.", 404),

  USER_STATUS_NOT_FOUND("S1000", "사용자 상태를 찾을 수 없습니다.", 404),
  USER_STATUS_ALREADY_EXISTS("S1001", "사용자 상태가 이미 존재합니다.", 409),

  MESSAGE_NOT_FOUND("M1000", "메시지를 찾을 수 없습니다.", 404),

  READ_STATUS_NOT_FOUND("R1000", "읽음 상태를 찾을 수 없습니다.", 404);

  private final String code;
  private final String message;
  private final int status;

  ErrorCode(String code, String message, int status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }
}