package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "사용자를 찾을 수 없습니다."),
  DUPLICATE_USER(HttpStatus.CONFLICT, "USER_409", "이미 존재하는 사용자입니다."),
  SELF_ROLE_CHANGE_NOT_ALLOWED(HttpStatus.CONFLICT, "USER_409", "자기 자신의 권한은 변경할 수 없습니다."),
  INITIAL_ADMIN_ROLE_CHANGE_NOT_ALLOWED(HttpStatus.CONFLICT, "USER_409",
      "초기 관리자 계정의 권한은 변경할 수 없습니다."),

  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "CHANNEL_404", "채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE(HttpStatus.BAD_REQUEST, "CHANNEL_400", "비공개 채널은 수정할 수 없습니다."),

  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_404", "메시지를 찾을 수 없습니다."),

  READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "READ_STATUS_404", "읽기 상태를 찾을 수 없습니다."),

  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_404", "파일을 찾을 수 없습니다."),
  BINARY_CONTENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "FILE_409", "이미 존재하는 파일입니다."),

  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_401", "비밀번호가 올바르지 않습니다."),
  AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_401", "인증이 필요합니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_403", "접근 권한이 없습니다."),

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 리소스를 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

}
