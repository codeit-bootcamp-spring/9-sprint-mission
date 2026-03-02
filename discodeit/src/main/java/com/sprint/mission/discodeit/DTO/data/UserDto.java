package com.sprint.mission.discodeit.DTO.data;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileId,
    Boolean online   // 접속 중인지 여부를 담음
) {

}