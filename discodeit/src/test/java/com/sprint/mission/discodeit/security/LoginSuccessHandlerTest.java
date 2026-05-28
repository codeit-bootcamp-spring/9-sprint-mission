package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class LoginSuccessHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler(objectMapper);

  @Test
  @DisplayName("onAuthenticationSuccess 성공: 200과 UserResponse JSON을 반환한다")
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
    assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(userResponse));
  }
}
