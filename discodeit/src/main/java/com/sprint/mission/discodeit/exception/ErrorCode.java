package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  //유저
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_USER("이미 존재하는 사용자입니다."),

  //채널
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다."),
  MESSAGE_NOT_FOUND("채널을 찾을 수 없습니다."),

  //파일
  BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
  BINARY_CONTENT_ALREADY_EXISTS("이미 존재하는 파일입니다."),

  //공통
  INVALID_NAME("입력 형식이 맞지 않습니다.");


  private final String message;
}
