package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.Map;

// 에러 발생 시간, 상태 코드, 메시지, 그리고 필드별 상세 에러(Validation 용)를 담는 레코드입니다.
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors
) {
}