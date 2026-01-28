package com.sprint.mission.discodeit.dto;

import java.util.UUID;

/**
 * 사용자 수정 요청 DTO
 * - 수정 대상 id + 수정값 묶음
 */
public record UserUpdateRequest(
        UUID id,
        String nickname,
        String phoneNumber,
        String password,
        UUID profileImageId // 선택(없으면 null)
) {}

