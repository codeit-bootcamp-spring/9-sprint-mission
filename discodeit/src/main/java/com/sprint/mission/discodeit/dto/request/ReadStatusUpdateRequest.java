package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull(message = "업데이트할 읽은 시간이 누락되었습니다.")
    Instant newLastReadAt
) {
}