package com.sprint.mission.discodeit.exception;

import java.util.List;

public class UserException extends DiscodeitException {

  private final List<ErrorDetail> details;

  public UserException(ErrorCode errorCode) {
    super(errorCode);
    this.details = List.of(); // 에러 디테일이 없을 때는 빈 리스트 반환
  }

  // Map 대신 List<ErrorDetail>을 받도록 수정!
  public UserException(ErrorCode errorCode, List<ErrorDetail> details) {
    super(errorCode); // 부모에게는 errorCode만 넘깁니다.
    this.details = details;
  }

  public List<ErrorDetail> getErrorDetails() {
    return details;
  }
}