package com.sprint.mission.discodeit.dto;

import java.util.UUID;

/**
 * 사용자 조회 응답 DTO
 * - 요구사항: 비밀번호 제외
 * - 요구사항: 온라인 상태 포함(추후 UserStatus 붙이면 online 채움)
 */
public record UserResponse(
        UUID id,
        String loginId,
        String username,
        String nickname,
        String phoneNumber,
        Boolean online,      // 지금은 null 또는 false로 둘 예정(서비스 고도화에서 채움)
        UUID profileImageId  // 선택(없으면 null)
) {}

