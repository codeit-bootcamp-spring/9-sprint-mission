package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.JwtInformation;

import java.util.UUID;

public interface JwtRegistry {
    JwtInformation registerJwtInformation(JwtInformation jwtInformation);

    boolean invalidateJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void clearExpiredJwtInformation();

}
