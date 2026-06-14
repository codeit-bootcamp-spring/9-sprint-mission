package com.sprint.mission.discodeit.dto.response;

public record JwtDto(
    String accessToken,
    String tokenType
) {
}
