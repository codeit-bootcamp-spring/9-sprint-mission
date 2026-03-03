package com.sprint.mission.discodeit.dto.request;

/**
 * 로그인 요청 DTO
 * 사용자 인증에 필요한 정보를 전달합니다.
 *
 * @param username 사용자명
 * @param password 비밀번호
 */
public record LoginRequest(
        String username,
        String password
) {
}
