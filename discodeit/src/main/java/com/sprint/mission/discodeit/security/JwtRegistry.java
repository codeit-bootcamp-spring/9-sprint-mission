package com.sprint.mission.discodeit.security;

import java.util.List;
import java.util.UUID;

public interface JwtRegistry {
  void registerJwtInformation(JwtInformation jwtInformation);
  void invalidateJwtInformationByUserId(UUID userId);
  boolean hasActiveJwtInformationByUserId(UUID userId);
  boolean hasActiveJwtInformationByAccessToken(String accessToken);
  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);
  void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);
  void clearExpiredJwtInformation();

  void invalidateJwtInformationByRefreshToken(String refreshToken);

  void invalidateJwtInformationByAccessToken(String accessToken);

  List<UUID> getActiveUserIds();
}
