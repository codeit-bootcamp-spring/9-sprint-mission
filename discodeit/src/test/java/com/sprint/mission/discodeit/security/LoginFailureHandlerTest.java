package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

class LoginFailureHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  private final LoginFailureHandler loginFailureHandler = new LoginFailureHandler(objectMapper);

  @Test
  @DisplayName("onAuthenticationFailure 성공: 401과 ErrorResponse JSON을 반환한다")
  void onAuthenticationFailure_success() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("username", "jun");
    MockHttpServletResponse response = new MockHttpServletResponse();

    loginFailureHandler.onAuthenticationFailure(
        request,
        response,
        new BadCredentialsException("Bad credentials")
    );

    ErrorResponse errorResponse = objectMapper.readValue(
        response.getContentAsString(),
        ErrorResponse.class
    );

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentType()).isEqualTo("application/json");
    assertThat(errorResponse.code()).isEqualTo("AUTH_401");
    assertThat(errorResponse.status()).isEqualTo(401);
    assertThat(errorResponse.details()).containsEntry("username", "jun");
  }
}
