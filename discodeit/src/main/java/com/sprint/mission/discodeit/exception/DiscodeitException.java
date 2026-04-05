package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage()); //부모클래스의 생성자 호출해서 자식에서 만든 메시지를 부모한테 전달. 부모 초기화
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details;
  }

}
