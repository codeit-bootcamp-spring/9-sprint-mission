package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;

/** record: 불변 + 데이터 전달 최적 */
@Builder
public record UserParams(
        String username,
        String displayName,
        String email,
        String phoneNumber,
        String password
) {
}
