package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

class LoginFailureHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  private final LoginFailureHandler handler = new LoginFailureHandler(objectMapper);

  @Test
  @DisplayName("인증 실패 시 401 ErrorResponse로 응답한다")
  void onAuthenticationFailure_WritesErrorResponse() throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();

    handler.onAuthenticationFailure(new MockHttpServletRequest(), response,
        new BadCredentialsException("Bad credentials"));

    JsonNode errorResponse = objectMapper.readTree(response.getContentAsString());
    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    assertThat(errorResponse.get("status").asInt()).isEqualTo(401);
    assertThat(errorResponse.get("exceptionType").asText()).isEqualTo("BadCredentialsException");
  }
}
