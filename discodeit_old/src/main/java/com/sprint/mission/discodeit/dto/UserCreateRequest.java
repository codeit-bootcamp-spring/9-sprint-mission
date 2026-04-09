package com.sprint.mission.discodeit.dto;

import java.util.UUID;

/**
 * 사용자 생성 요청 DTO
 * - 요구사항: username, email만으로는 부족하고 password/phone/프로필이미지까지 고려 가능
 * - 지금은 기존 User 생성자 파라미터 기준으로 묶음
 */
public record UserCreateRequest(
        String loginId,
        String password,
        String username,
        String phoneNumber,
        String nickname,
        UUID profileImageId // 선택(없으면 null)
) {}

