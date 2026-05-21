package com.sprint.mission.discodeit.dto.data;

public record JwtDto(
    String accessToken,
    UserDto userDto
) {

}
