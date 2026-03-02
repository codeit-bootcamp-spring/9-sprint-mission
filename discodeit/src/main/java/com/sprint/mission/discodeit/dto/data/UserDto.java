package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    Boolean online,
    Instant lastActive
) {

  public static UserDto from(User user) {
    boolean isOnline = (user.getUserStatus() != null) && user.getUserStatus().getOnline();
    Instant lastActive =
        (user.getUserStatus() != null) ? user.getUserStatus().getLastActiveAt() : Instant.now();

    return new UserDto(
        user.getId(),
        user.getUsername(),
        isOnline,
        lastActive
    );
  }
}