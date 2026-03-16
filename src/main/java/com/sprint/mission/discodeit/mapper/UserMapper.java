package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.time.Duration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "online", expression = "java(isOnline(user.getStatus()))")

  UserDto toDto(User user);

  default Boolean isOnline(UserStatus status) {
    if (status == null) return null;
    return Duration.between(status.getLastActiveAt(), Instant.now())
        .compareTo(Duration.ofMinutes(5)) <= 0;
  }


}