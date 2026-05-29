package com.sprint.mission.discodeit.security.jwt.store;

import java.util.UUID;

public interface JwtRegistry {
  void registerJwtInformation(JwtInformation jwtInformation);
  void invalidateJwtInformationByUserId(UUID userId);
  boolean hasActiveJwtInformationByUserId(UUID userId);
  boolean hasActiveJwtInformationByAccessToken(String accessToken);
  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);
  boolean rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);
  void clearExpiredJwtInformation();
  void invalidateJwtInformationByRefreshToken(String refreshToken);
}

