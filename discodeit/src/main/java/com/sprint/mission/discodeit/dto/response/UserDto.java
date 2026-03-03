package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 목록 조회용 DTO
 * 사용자 정보와 온라인 상태를 포함합니다.
 *
 * @param id 사용자 ID
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @param username 사용자명
 * @param email 이메일 주소
 * @param profileId 프로필 이미지 ID
 * @param online 온라인 여부 (Boolean 타입)
 */
public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online
) {
}
