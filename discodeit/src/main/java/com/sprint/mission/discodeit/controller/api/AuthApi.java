package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

public interface AuthApi {
  @GetMapping("/api/auth/me")
  ResponseEntity<UserDto> getMe(@AuthenticationPrincipal DiscodeitUserDetails userDetails);
}
