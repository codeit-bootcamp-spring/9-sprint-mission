package com.sprint.mission.discodeit.dto.request;

/**
 * 사용자 정보 수정 요청 DTO
 * 사용자의 정보를 변경할 때 사용합니다.
 *
 * @param newUsername 변경할 사용자명
 * @param newEmail 변경할 이메일 주소
 * @param newPassword 변경할 비밀번호
 */
public record UserUpdateRequest(
        String newUsername,
        String newEmail,
        String newPassword
) {
}
