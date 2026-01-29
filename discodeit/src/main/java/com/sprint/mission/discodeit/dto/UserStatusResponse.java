package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        UUID userId,
        Instant lastSeenAt,
        boolean isOnline // 온라인 여부 계산 결과도 포함
) {}