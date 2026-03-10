package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

  public UserDto toDto(User user) {
    BinaryContentDto profileDto = null;
    if (user.getProfile() != null) {
      profileDto = toBinaryContentDto(user.getProfile());
    }

    Boolean online = null;
    UserStatus status = user.getStatus();
    if (status != null) {
      online = isOnline(status.getLastActiveAt());
    }

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profileDto,
        online
    );
  }

  private BinaryContentDto toBinaryContentDto(BinaryContent binaryContent) {
    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType()
    );
  }

  private boolean isOnline(Instant lastActiveAt) {
    return Duration.between(lastActiveAt, Instant.now())
        .compareTo(ONLINE_THRESHOLD) <= 0;
  }
}