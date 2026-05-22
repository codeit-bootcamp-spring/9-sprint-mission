package com.sprint.mission.discodeit.dto.data;

public record RefreshTokenResultDto(
    JwtDto jwtDto,
    String newRefreshToken
) {
}
