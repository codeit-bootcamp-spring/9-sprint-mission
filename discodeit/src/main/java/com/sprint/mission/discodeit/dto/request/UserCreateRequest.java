package com.sprint.mission.discodeit.dto.request;

/**
 * 사용자 생성 요청 DTO
 * 회원가입 시 사용합니다.
 *
 * @param username 사용자명
 * @param email 이메일 주소
 * @param password 비밀번호
 */
public record UserCreateRequest(
        String username,
        String email,
        String password
) {
}
