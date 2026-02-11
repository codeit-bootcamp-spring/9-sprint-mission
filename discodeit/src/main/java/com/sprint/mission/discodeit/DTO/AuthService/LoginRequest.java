package com.sprint.mission.discodeit.DTO.AuthService;

public record LoginRequest(
        String userName,
        String password
) {
}
