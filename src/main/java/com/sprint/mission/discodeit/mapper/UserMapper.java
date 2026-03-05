package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  public UserDto toDto(User user, boolean online) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getName(),
        user.getEmail(),
        user.getProfileId(),
        online
    );
  }
}