package com.sprint.mission.discodeit.dto.auth;

public record LoginRequest(
        String userName,
        String password
) {
}
