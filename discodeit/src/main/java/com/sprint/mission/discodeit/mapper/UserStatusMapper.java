package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  public UserStatusDto toDto(UserStatus status) {
    return new UserStatusDto(
        status.getUser().getId(),
        status.getLastActiveAt(),
        status.isOnline()
    );
  }
}