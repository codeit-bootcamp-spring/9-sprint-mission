package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // USER
  USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다", 404),
  USER_EMAIL_DUPLICATED("USER_002", "이미 존재하는 이메일입니다", 409),
  USER_USERNAME_DUPLICATED("USER_003", "이미 존재하는 사용자명입니다", 409),
  USER_INVALID_PASSWORD("USER_004", "잘못된 비밀번호입니다", 401),

  // CHANNEL
  CHANNEL_NOT_FOUND("CHANNEL_001", "채널을 찾을 수 없습니다", 404),
  CHANNEL_PRIVATE_UPDATE_FORBIDDEN("CHANNEL_002", "PRIVATE 채널은 수정할 수 없습니다", 403),

  // MESSAGE
  MESSAGE_NOT_FOUND("MESSAGE_001", "메시지를 찾을 수 없습니다", 404),

  // BINARY CONTENT (파일)
  BINARY_CONTENT_NOT_FOUND("BINARY_001", "파일을 찾을 수 없습니다", 404),
  BINARY_CONTENT_UPLOAD_FAILED("BINARY_002", "파일 업로드에 실패했습니다", 400),
  BINARY_CONTENT_DOWNLOAD_FAILED("BINARY_003", "파일 다운로드에 실패했습니다", 400),

  // VALIDATION
  INVALID_REQUEST("VALIDATION_001", "유효하지 않은 요청입니다", 400),

  // INTERNAL SERVER ERROR
  INTERNAL_SERVER_ERROR("INTERNAL_001", "서버 내부 오류가 발생했습니다", 500);

  private final String code;
  private final String message;
  private final int httpStatus;
}