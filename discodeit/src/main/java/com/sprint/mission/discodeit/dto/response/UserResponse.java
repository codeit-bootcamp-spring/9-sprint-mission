package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 응답 DTO (User 생성/수정/로그인 시 반환)
 * API 스펙의 User 스키마에 대응합니다.
 *
 * @param id 사용자 ID
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @param username 사용자명
 * @param email 이메일 주소
 * @param password 비밀번호
 * @param profileId 프로필 이미지 ID
 */
public record UserResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        String password,
        UUID profileId
) {
}
