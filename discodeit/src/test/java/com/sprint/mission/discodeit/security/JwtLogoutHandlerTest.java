package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtLogoutHandlerTest {

  private final JwtRegistry jwtRegistry = Mockito.mock(JwtRegistry.class);
  private final JwtLogoutHandler jwtLogoutHandler = new JwtLogoutHandler(jwtRegistry);

  @Test
  @DisplayName("logout 성공: REFRESH_TOKEN 쿠키를 즉시 만료시킨다")
  void logout_success_expiresRefreshTokenCookie() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, "refresh-token"));
    MockHttpServletResponse response = new MockHttpServletResponse();

    jwtLogoutHandler.logout(request, response, null);

    assertThat(response.getHeader(HttpHeaders.SET_COOKIE))
        .contains(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME + "=")
        .contains("Max-Age=0")
        .contains("HttpOnly")
        .contains("SameSite=Lax");
    then(jwtRegistry).should().invalidateJwtInformationByRefreshToken("refresh-token");
  }
}
