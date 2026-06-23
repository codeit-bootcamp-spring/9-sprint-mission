package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ApplicationEventPublisher eventPublisher;

  @CacheEvict(value = "UserList", key = "'all_users'")
  @Override
  @Transactional(readOnly = true)
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    if (request.getCookies() == null) {
      return;
    }

    Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
        .findFirst()
        .ifPresent(cookie -> {
          jwtRegistry.findUserIdByRefreshToken(cookie.getValue())
              .ifPresent(userId -> {
                jwtRegistry.invalidateJwtInformationByUserId(userId);
                UserDto updatedDto = userMapper.toDto(userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId)));
                eventPublisher.publishEvent(new UserUpdatedEvent("updated", updatedDto));
              });

          Cookie expiredCookie = new Cookie("REFRESH_TOKEN", null);
          expiredCookie.setHttpOnly(true);
          expiredCookie.setPath("/api/auth");
          expiredCookie.setMaxAge(0);
          response.addCookie(expiredCookie);
        });
  }
}