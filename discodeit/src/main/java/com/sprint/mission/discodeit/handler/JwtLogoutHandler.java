package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.service.SseService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final SseService sseService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    String refreshToken = extractRefreshToken(request);

    if (refreshToken != null) {
      Optional<JwtInformation> jwtInfoOpt = jwtRegistry.getJwtInformationByRefreshToken(refreshToken);
      jwtRegistry.invalidateJwtInformationByRefreshToken(refreshToken);

      jwtInfoOpt.ifPresent(jwtInfo -> {
        UserDto userDto = jwtInfo.getUserDto();
        UserDto offlineUserDto = new UserDto(
            userDto.id(),
            userDto.username(),
            userDto.email(),
            userDto.profile(),
            false,
            userDto.role()
        );
        sseService.broadcast("users.updated", offlineUserDto);
      });
    }
  }

  private String extractRefreshToken(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }

  private void clearCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("REFRESH_TOKEN", null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }
}
