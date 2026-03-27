package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(target = "profile", source = "profile")
  @Mapping(target = "online", source = "status", qualifiedByName = "mapOnline")
  UserResponse toResponse(User user);

  @Named("mapOnline")
  default Boolean mapOnline(UserStatus status) {
    return status != null && status.isOnline();
  }
}