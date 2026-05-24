package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  private SessionRegistry sessionRegistry;

  @Mapping(target = "online", expression = "java(isOnline(user.getUsername()))")
  @Mapping(target = "profile", source = "profile")
  public abstract UserDto toDto(User user);

  protected boolean isOnline(String username) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(p -> p instanceof DiscodeitUserDetails)
        .map(p -> (DiscodeitUserDetails) p)
        .filter(u -> u.getUsername().equals(username))
        .flatMap(u -> sessionRegistry.getAllSessions(u, false).stream())
        .findAny()
        .isPresent();
  }
}