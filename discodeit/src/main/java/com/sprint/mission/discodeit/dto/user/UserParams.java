package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;

/** - Java record: 불변(immutable) + 데이터 전달에 최적 */
@Builder
public record UserParams(
        String displayName,
        String email,
        String phoneNumber
) {

}
