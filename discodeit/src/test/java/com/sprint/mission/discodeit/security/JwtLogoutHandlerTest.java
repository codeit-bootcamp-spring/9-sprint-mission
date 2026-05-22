package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtLogoutHandlerTest {

  private final JwtLogoutHandler jwtLogoutHandler = new JwtLogoutHandler();

  @Test
  @DisplayName("logout 성공: REFRESH_TOKEN 쿠키를 즉시 만료시킨다")
  void logout_success_expiresRefreshTokenCookie() {
    MockHttpServletResponse response = new MockHttpServletResponse();

    jwtLogoutHandler.logout(new MockHttpServletRequest(), response, null);

    assertThat(response.getHeader(HttpHeaders.SET_COOKIE))
        .contains(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME + "=")
        .contains("Max-Age=0")
        .contains("HttpOnly")
        .contains("SameSite=Lax");
  }
}
