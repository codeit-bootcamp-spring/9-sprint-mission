package com.sprint.mission.discodeit.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
  CHANNEL_NOT_FOUND(404, "채널을 찾을 수 없습니다."),
  MESSAGE_NOT_FOUND(404, "메시지를 찾을 수 없습니다."),
  USER_STATUS_NOT_FOUND(404, "유저 상태를 찾을 수 없습니다."),
  READ_STATUS_NOT_FOUND(404, "읽음 상태를 찾을 수 없습니다."),
  PARTICIPANTS_NOT_FOUND(404, "일부 유저가 존재하지 않습니다."),
  BINARY_CONTENT_NOT_FOUND(404, "해당 이미지나 프로필을 찾을 수 없습니다."),


  DUPLICATE_USER(409, "이미 존재하는 유저입니다."),
  DUPLICATE_EMAIL(409, "이미 존재하는 이메일입니다."),
  DUPLICATE_CHANNEL(409, "이미 존재하는 채널이름입니다."),
  DUPLICATE_USER_STATUS(409, "이미 존재하는 유저 상태입니다."),
  DUPLICATE_READ_STATUS(409, "해당 유저의 읽음 상태가 이미 존재합니다"),
  DUPLICATE_BINARY_CONTENT(409, "이미 존재하는 사진파일입니다."),
  DUPLICATE_USER_AND_CHANNEL(404, "유저와 채널이 이미 존재합니다."),


  PRIVATE_CHANNEL_UPDATE(400, "프라이빗 채널은 수정이 불가합니다."),
  WRONG_PASSWORD(400, "아이디 또는 비밀번호가 일치하지 않습니다."),


  FAIL_SAVE_FILE(500, "해당 파일 저장을 실패했습니다."),
  INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
  NOTIFICATION_NOT_FOUND(404, "알림을 찾을 수 없습니다."),
  NOTIFICATION_ACCESS_DENIED(403, "요청자 본인의 알림에 대해서만 수행할 수 있습니다.");


  private final int status;
  private final String message;
}
