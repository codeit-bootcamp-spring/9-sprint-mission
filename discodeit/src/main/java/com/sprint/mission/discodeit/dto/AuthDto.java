package com.sprint.mission.discodeit.dto;

public interface AuthDto {
    // 로그인 요청
    record LoginRequest(
            String email,
            String password
    ) {}
}