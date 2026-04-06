package com.sprint.mission.discodeit.global;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public record ErrorResponse(
    Instant timestamp, String code, String message, Map<String, Object> details,
    String exceptionType, int status
) {

  public static ErrorResponse from(DiscodeitException e) {
    ErrorCode code = e.getErrorCode();
    return new ErrorResponse(
        e.getTimestamp(),
        code.name(),
        code.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        code.getStatus()
    );
  }

  public static ErrorResponse of(MethodArgumentNotValidException e) {
    Map<String, Object> details = e.getBindingResult()
        .getFieldErrors() // 발생한 모든 필드 에러를 가져옴
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,       // key: 필드명 (예: "email")
            FieldError::getDefaultMessage, // value: 에러 메시지 (예: "이메일 형식이 올바르지 않습니다")
            (existing, replacement) -> existing // 중복 필드 에러 시 첫 번째 것 유지
        ));

    return new ErrorResponse(
        Instant.now(),
        "INVALID_INPUT_VALUE",
        e.getFieldError() != null ? e.getFieldError().getField() + "의 형식이 맞지 않습니다."
            : "입력값 검증에 실패했습니다.",
        details,
        e.getClass().getSimpleName(),
        400
    );
  }
}
