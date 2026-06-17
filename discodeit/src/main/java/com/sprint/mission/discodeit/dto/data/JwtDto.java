package com.sprint.mission.discodeit.dto.data;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {
}