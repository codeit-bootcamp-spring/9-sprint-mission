package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class JwtLoginSuccessHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
      "test-token-secret-key-for-hs256-32bytes",
      3600
  );
  private final JwtRegistry jwtRegistry = new InMemoryJwtRegistry(1);
  private final JwtTokenIssuer jwtTokenIssuer = new JwtTokenIssuer(jwtTokenProvider, jwtRegistry);
  private final JwtLoginSuccessHandler jwtLoginSuccessHandler = new JwtLoginSuccessHandler(
      objectMapper,
      jwtTokenIssuer
  );

  @Test
  @DisplayName("onAuthenticationSuccess 성공: 200과 토큰 포함 JSON을 반환한다")
  void onAuthenticationSuccess_success() throws Exception {
    UserResponse userResponse = new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, false);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponse, "encodedPassword");
    UsernamePasswordAuthenticationToken authentication =
        UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
    MockHttpServletResponse response = new MockHttpServletResponse();

    jwtLoginSuccessHandler.onAuthenticationSuccess(
        new MockHttpServletRequest(),
        response,
        authentication
    );

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getContentType()).isEqualTo("application/json");
    assertThat(response.getHeader("Authorization")).startsWith("Bearer ");

    JwtDto body = objectMapper.readValue(response.getContentAsString(), JwtDto.class);
    assertThat(body.accessToken()).isNotBlank();
    assertThat(body.tokenType()).isEqualTo("Bearer");
    assertThat(body.userDto().username()).isEqualTo("jun");
    assertThat(body.userDto().email()).isEqualTo("jun@test.com");
    assertThat(response.getCookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME)).isNotNull();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(body.accessToken())).isTrue();
  }
}
