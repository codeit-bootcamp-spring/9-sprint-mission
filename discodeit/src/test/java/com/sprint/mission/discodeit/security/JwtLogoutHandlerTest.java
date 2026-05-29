package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtLogoutHandlerTest {

  @Mock
  private JwtRegistry jwtRegistry;

  @Test
  void logoutDeletesRefreshTokenCookieAndInvalidatesJwtInformation() {
    JwtLogoutHandler logoutHandler = new JwtLogoutHandler(jwtRegistry);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setCookies(new Cookie("REFRESH_TOKEN", "refresh-token"));

    logoutHandler.logout(request, response, null);

    Cookie cookie = response.getCookie("REFRESH_TOKEN");
    assertThat(cookie).isNotNull();
    assertThat(cookie.getValue()).isNull();
    assertThat(cookie.getMaxAge()).isZero();
    assertThat(cookie.getPath()).isEqualTo("/");
    assertThat(cookie.isHttpOnly()).isTrue();
    then(jwtRegistry).should().invalidateJwtInformationByRefreshToken("refresh-token");
  }
}
