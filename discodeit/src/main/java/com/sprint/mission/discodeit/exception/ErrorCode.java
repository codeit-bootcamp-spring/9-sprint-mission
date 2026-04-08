package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User with id %s not found"),
  USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U002", "User with email %s already exists"),
  USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "UserStatus with userId %s not found"),
  USER_USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "U004", "User with username %s not found"),
  WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "A001", "Wrong password"),


  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "Channel with id %s not found"),
  PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "C002",
      "Private channel cannot be updated"),


  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "Message with id %s not found"),
  READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "ReadStatus with id %s not found"),
  READ_STATUS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "R002",
      "ReadStatus with userId %s and channelId %s already exists"),


  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "BinaryContent with id %s not found"),


  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "V002", "잘못된 입력값입니다."),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "G001", "허용되지 않은 HTTP 메서드입니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "F001", "해당 작업에 대한 권한이 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}