package com.sprint.mission.discodeit.dto;

public record UserUpdateRequest(
        String userName,
        String email,
        String password
) {}
