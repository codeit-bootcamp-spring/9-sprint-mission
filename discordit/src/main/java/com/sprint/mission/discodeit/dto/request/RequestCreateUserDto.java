package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public record RequestCreateUserDto(
    String username,
    String password,
    String email
) {

  public User toEntity(UUID profileId) {

    return new User(username, password, email, profileId);
  }
}
