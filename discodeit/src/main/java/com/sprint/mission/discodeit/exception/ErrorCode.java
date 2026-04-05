package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  INVALID_INPUT(400, "COM_001", "입력값이 올바르지 않습니다."),
  INTERNAL_SERVER_ERROR(500, "COM_999", "서버 내부 오류가 발생했습니다."),

  USER_NOT_FOUND(404, "USR_001", "사용자를 찾을 수 없습니다."),
  DUPLICATE_USER(400, "USR_002", "이미 존재하는 사용자입니다."),

  CHANNEL_NOT_FOUND(404, "CHN_001", "채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE(400, "CHN_002", "비공개 채널은 수정할 수 없습니다."),

  MESSAGE_NOT_FOUND(404, "MSG_001", "메시지를 찾을 수 없습니다."),

  BINARY_CONTENT_NOT_FOUND(404, "BIN_001", "파일을 찾을 수 없습니다.");

  private final int Status;
  private final String code;
  private final String message;
}