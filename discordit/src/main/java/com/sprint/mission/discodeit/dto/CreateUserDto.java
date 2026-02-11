package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record CreateUserDto(
        String username,
        String password,
        UUID profileId
) {
    public User toEntity() {

        return new User(username, password, profileId);
    }
}
