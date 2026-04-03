package com.sprint.mission.discodeit.error;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

  private final Instant timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;

  // 도메인 예외용 (인자 1개 버전)
  public static ErrorResponse of(DiscodeitException e) {
    return ErrorResponse.builder()
        .timestamp(e.getTimestamp())
        .code(e.getErrorCode().name())
        .message(e.getErrorCode().getMessage())
        .details(e.getDetails())
        .exceptionType(e.getClass().getSimpleName())
        .status(e.getErrorCode().getHttpStatus().value()) // 에러코드에서 상태 추출
        .build();
  }

  // 검증 실패용 (List<String>을 받도록 구현)
  public static ErrorResponse validationFailed(List<String> errors) {
    Map<String, Object> details = new HashMap<>();
    details.put("errors", errors);

    return ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("VALIDATION_FAILED")
        .message("입력 데이터 검증 실패")
        .details(details)
        .exceptionType("MethodArgumentNotValidException")
        .status(400)
        .build();
  }

  // 시스템 오류용
  public static ErrorResponse systemError() {
    return ErrorResponse.builder()
        .timestamp(Instant.now())
        .code("INTERNAL_SERVER_ERROR")
        .message("서버 내부 오류가 발생했습니다.")
        .exceptionType("Exception")
        .status(500)
        .build();
  }
}