package com.sprint.mission.discodeit.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Builder;

@Builder
@JsonIgnoreProperties("refreshToken")
public record JwtDto(
    UserDto userDto,
    String accessToken,
    String refreshToken
) {

}
