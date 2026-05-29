package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class}) // 1. UserStatusMapper 제거
public interface UserMapper {

  @Mapping(target = "online", ignore = true)
  UserDto toDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true) // 2. status 매핑 설정을 제거 (이제 User에 status 필드가 없으므로 필요 없음)
  @Mapping(target = "profile", ignore = true)
  @Mapping(target = "password", ignore = true)
  void updateFromRequest(UserCreateRequest request, @MappingTarget User user);
}
//@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
//public interface UserMapper {
//
//  @Mapping(target = "online", ignore = true)
//  UserDto toDto(User user);
//
//  @Mapping(target = "id", ignore = true)
//  @Mapping(target = "status", ignore = true)
//  void updateFromRequest(UserCreateRequest request, @MappingTarget User user);
//}