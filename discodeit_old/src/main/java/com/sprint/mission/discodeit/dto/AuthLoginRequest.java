package com.sprint.mission.discodeit.dto;

public record AuthLoginRequest(
        String username, // 요구사항 문구가 username이라 일단 그대로
        String password
) {}

