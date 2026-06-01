package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

public interface AuthService {

  void invalidateUserSessionsByUserId(UUID userId);

  JwtDto refresh(String refreshToken, HttpServletResponse response);
}
