package com.sprint.mission.discodeit.dto;

public record LoginRequest(
        String username,
        String password
) {}

// 사용자의 아이디(이름), 사용자의 비밀번호