package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.data.RefreshTokenResultDto;

public interface AuthService {

  RefreshTokenResultDto refreshAccessToken(String refreshToken);
}
