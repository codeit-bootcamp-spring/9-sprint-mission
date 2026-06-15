package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;

@Builder
public record JwtDto(UserDto userDto, String accessToken) {
}
