// UserStatusMapper
package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

  public UserStatusDto toDto(UserStatus userStatus) {
    return new UserStatusDto(
        userStatus.getId(),
        userStatus.getUser().getId(),
        userStatus.getLastActiveAt(),
        isOnline(userStatus.getLastActiveAt())
    );
  }

  private boolean isOnline(Instant lastActiveAt) {
    return Duration.between(lastActiveAt, Instant.now())
        .compareTo(ONLINE_THRESHOLD) <= 0;
  }
}
