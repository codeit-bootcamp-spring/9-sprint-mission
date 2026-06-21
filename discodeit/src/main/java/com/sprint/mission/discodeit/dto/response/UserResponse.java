package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    Boolean online,
    UserRole role
) {

  public UserResponse(
      UUID id,
      String username,
      String email,
      BinaryContentResponse profile,
      Boolean online
  ) {
    this(id, username, email, profile, online, UserRole.USER);
  }
}
