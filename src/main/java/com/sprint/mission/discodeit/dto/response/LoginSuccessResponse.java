package com.sprint.mission.discodeit.dto.response;

import lombok.Builder;

@Builder
public record LoginSuccessResponse(
        boolean success, String accessToken, String tokenType, String username
) {
}
