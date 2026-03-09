package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  public UserStatusDto toDto(UserStatus entity) {
    if (entity == null) {
      return null;
    }

    return new UserStatusDto(
        entity.getId(),
        entity.getUser().getId(),
        entity.getLastActiveAt()

    );
  }
}