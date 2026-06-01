package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void invalidateUserSessionsByUserId(UUID userId) {
    jwtRegistry.invalidateJwtInformationByUserId(userId);
  }

  @Override
  @Transactional
  public JwtDto refresh(String token, HttpServletResponse response) {
    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(token)) {
      throw new DiscodeitException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    JwtInformation jwtInformation = jwtRegistry.getJwtInformationByRefreshToken(token)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.INVALID_REFRESH_TOKEN));

    UserDto userDto = jwtInformation.getUserDto();
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "");

    String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
    String newRefreshTokenValue = jwtTokenProvider.generateRefreshToken();

    JwtInformation newJwtInformation = new JwtInformation(userDto, newAccessToken,
        newRefreshTokenValue);
    jwtRegistry.rotateJwtInformation(token, newJwtInformation);

    setRefreshTokenCookie(response, newRefreshTokenValue);

    return new JwtDto(userDto, newAccessToken);
  }

  private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) jwtTokenProvider.getRefreshTokenValiditySeconds());
    response.addCookie(cookie);
  }
}
