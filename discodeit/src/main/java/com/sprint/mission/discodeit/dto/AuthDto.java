package com.sprint.mission.discodeit.dto;

public interface AuthDto {
    record LoginRequest(
            String email,
            String password
    ) {}
}