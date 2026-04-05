package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotNull(message = "업데이트할 마지막 활동 시간이 누락되었습니다.")
    Instant newLastActiveAt
) {
}