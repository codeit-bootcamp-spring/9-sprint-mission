package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {

  // 수정할 수 없도록 final 선언
  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  // 상세 정보(details) 없이 에러 코드만 발생시킬 때 쓰는 생성자
  public DiscodeitException(ErrorCode errorCode) {
    super(errorCode.getMessage()); // RuntimeException에 메시지 전달
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = new HashMap<>(); // 빈 맵으로 초기화 (null 방지)
  }

  // 에러 코드와 상세 정보(details)를 같이 발생시킬 때 쓰는 생성자
  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details != null ? details : new HashMap<>();
  }
}