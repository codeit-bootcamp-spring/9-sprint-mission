package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class LoginSuccessHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
      "test-token-secret-key-for-hs256-32bytes",
      3600
  );
  private final LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler(
      objectMapper,
      jwtTokenProvider
  );

  @Test
  @DisplayName("onAuthenticationSuccess 성공: 200과 토큰 포함 JSON을 반환한다")
  void onAuthenticationSuccess_success() throws Exception {
    UserResponse userResponse = new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, false);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponse, "encodedPassword");
    UsernamePasswordAuthenticationToken authentication =
        UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
    MockHttpServletResponse response = new MockHttpServletResponse();

    loginSuccessHandler.onAuthenticationSuccess(
        new MockHttpServletRequest(),
        response,
        authentication
    );

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getContentType()).isEqualTo("application/json");
    assertThat(response.getHeader("Authorization")).startsWith("Bearer ");

    Map<String, Object> body = objectMapper.readValue(response.getContentAsString(), Map.class);
    assertThat(body.get("accessToken")).isInstanceOf(String.class);
    assertThat(body.get("tokenType")).isEqualTo("Bearer");
    assertThat(body.get("username")).isEqualTo("jun");
    assertThat(response.getCookie("refreshToken")).isNotNull();
    assertThat((Map<String, Object>) body.get("user"))
        .containsEntry("username", "jun")
        .containsEntry("email", "jun@test.com");
    assertThat((Map<String, Object>) body.get("userDto"))
        .containsEntry("username", "jun")
        .containsEntry("email", "jun@test.com");
  }
}
