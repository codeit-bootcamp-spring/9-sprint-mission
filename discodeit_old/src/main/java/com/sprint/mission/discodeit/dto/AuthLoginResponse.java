package com.sprint.mission.discodeit.dto;

import java.util.UUID;

/**
 * 로그인 성공 시 반환할 사용자 정보(간단 버전)
 */
public record AuthLoginResponse(
        UUID userId,
        String username,
        String nickname
) {}

