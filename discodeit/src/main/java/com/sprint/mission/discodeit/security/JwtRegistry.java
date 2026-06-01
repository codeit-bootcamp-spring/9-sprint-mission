package com.sprint.mission.discodeit.security;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface JwtRegistry {

  void registerJwtInformation(JwtInformation jwtInformation);

  void invalidateJwtInformationByUserId(UUID userId);

  void invalidateJwtInformationByRefreshToken(String refreshToken);

  boolean hasActiveJwtInformationByUserId(UUID userId);

  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  Optional<JwtInformation> getJwtInformationByRefreshToken(String refreshToken);

  Set<UUID> getAllOnlineUserIds();

  void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation);

  void clearExpiredJwtInformation();
}
