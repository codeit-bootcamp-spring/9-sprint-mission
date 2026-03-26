package com.sprint.mission.discodeit.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
  DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),

  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE(HttpStatus.FORBIDDEN, "비공개 채널은 수정할 수 없습니다."),

  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 메시지를 찾을 수 없습니다."),

  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파일을 찾을 수 없습니다."),
  FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  ErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getMessage() {
    return message;
  }
}