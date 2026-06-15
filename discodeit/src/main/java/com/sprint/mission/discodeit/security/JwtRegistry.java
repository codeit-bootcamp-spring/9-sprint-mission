package com.sprint.mission.discodeit.security;

import java.util.Optional;
import java.util.UUID;

public interface JwtRegistry {

  void registerJwtInformation(JwtInformation jwtInformation);

  void invalidateJwtInformationByUserId(UUID userId);

  boolean hasActiveJwtInformationByUserId(UUID userId);

  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  Optional<JwtInformation> findByRefreshToken(String refreshToken);

  JwtInformation rotateJwtInformation(String refreshToken, String newAccessToken,
      String newRefreshToken);

  void clearExpiredJwtInformation();
}
