package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;

@Builder
public record UserParams(
        String username,
        String displayName,
        String email,
        String phoneNumber,
        String password
) {
}
