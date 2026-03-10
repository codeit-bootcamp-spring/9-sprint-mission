package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(target = "online", source = "status.online")
  UserDto toDto(User user);

  default boolean mapOnline(UserStatus status) {
    if (status == null) {
      return false;
    }
    return status.isOnline();
  }
}