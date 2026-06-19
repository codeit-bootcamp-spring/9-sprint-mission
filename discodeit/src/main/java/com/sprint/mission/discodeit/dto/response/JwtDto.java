package com.sprint.mission.discodeit.dto.response;

public record JwtDto(
    UserResponse userDto,
    String accessToken,
    String tokenType,
    String expiresAt
) {

}
