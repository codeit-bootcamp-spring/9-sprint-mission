package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotNull(message = "사용자 ID가 누락되었습니다.")
    UUID userId,

    @NotNull(message = "마지막 활동 시간이 누락되었습니다.")
    Instant lastActiveAt
) {
}