package com.sprint.mission.discodeit.error;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  // 1. 기본 생성자 (추가 정보가 없을 때)
  public DiscodeitException(ErrorCode errorCode) {
    this(errorCode, new HashMap<>());
  }

  // 2. 상세 정보를 포함하는 생성자
  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage()); // 부모인 RuntimeException에 메시지 전달
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    // 외부에서 변경할 수 없도록 방어적 복사 수행
    this.details = Collections.unmodifiableMap(new HashMap<>(details));
  }
}