package com.sprint.mission.discodeit.secure;

import java.util.Optional;
import java.util.UUID;

public interface JwtRegistry {

  void registerJwtInformation(UUID userId, JwtInformation info);

  void invalidateJwtInformationByUserId(UUID userId);

  boolean hasActiveJwtInformationByUserId(UUID userId);

  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  void rotateJwtInformation(UUID userId, JwtInformation oldInfo, JwtInformation newInfo);

  void clearExpiredJwtInformation();

  Optional<JwtInformation> findByRefreshToken(String refreshToken);
}
