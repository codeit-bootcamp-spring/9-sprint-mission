package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.entity.JwtSession;
import java.util.UUID;

public interface AuthService {

  JwtSession saveSession(UUID userId, String accessToken, String refreshToken);

  JwtDto refresh(String refreshToken);

  void deleteSession(UUID userId);

}
