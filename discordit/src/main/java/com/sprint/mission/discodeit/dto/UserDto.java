package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.response.ResponseUserDto;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online
) {

    public static UserDto from(ResponseUserDto user, Boolean online) {
        return new UserDto(user.id(), user.createdAt(), user.updatedAt(), user.username(), user.email(), user.profileId(), online);
    }
}
