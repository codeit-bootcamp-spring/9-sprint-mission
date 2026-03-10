package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  /**
   * [진짜 최종 - 오타 및 타입 오류 완전 해결] 1. user.getStatus()가 null인지 먼저 확인 2. null이 아니면 status.isOnline() 값을
   * 그대로 사용 (boolean 기본형이라 null 체크 불필요) 3. status가 null이면 기본값 false 반환
   */
  @Mapping(target = "online", expression = "java(user.getStatus() != null ? user.getStatus().isOnline() : false)")
  @Mapping(target = "profile", source = "profile")
  UserDto toDto(User user);

  java.util.List<UserDto> toDtoList(java.util.List<User> users);
}