package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;


@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {


  @Mapping(target = "online", expression = "java(user.getStatus() != null ? user.getStatus().isOnline() : false)")
  @Mapping(target = "profile", source = "profile")
  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> users);
}