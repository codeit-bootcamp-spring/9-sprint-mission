package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        BinaryContentDto profile, // profileId -> profile 객체로 변경
        boolean online
) {
}