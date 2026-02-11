package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID id,
        Instant readAt // 언제 읽었는지 시간 업데이트
) {}