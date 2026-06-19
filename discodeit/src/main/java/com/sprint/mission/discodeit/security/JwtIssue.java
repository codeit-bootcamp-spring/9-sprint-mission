package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.JwtDto;
import org.springframework.http.ResponseCookie;

public record JwtIssue(
    JwtDto body,
    String accessToken,
    ResponseCookie refreshTokenCookie
) {
}
