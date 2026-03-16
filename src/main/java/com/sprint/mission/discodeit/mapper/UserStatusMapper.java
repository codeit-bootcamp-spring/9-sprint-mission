package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Duration;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "online", expression = "java(isOnline(userStatus.getLastActiveAt()))")
  UserStatusDto toDto(UserStatus userStatus);

  default Boolean isOnline(Instant lastActiveAt) {
    return Duration.between(lastActiveAt, Instant.now())
        .compareTo(Duration.ofMinutes(5)) <= 0;
  }
}
