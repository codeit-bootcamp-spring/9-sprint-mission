package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        UUID profileId,
        boolean online
) {
    public static UserResponse from(User user, UserStatus status) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileId(),
                status != null && status.isOnline()
        );
    }
}
