package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  protected SessionRegistry sessionRegistry;

  @Mapping(target = "online", expression = "java(checkOnlineStatus(user))")
  public abstract UserDto toDto(User user);

  protected boolean checkOnlineStatus(User user) {
    if (sessionRegistry == null) return false;

    List<Object> principals = sessionRegistry.getAllPrincipals();
    for (Object principal : principals) {
      if (principal instanceof DiscodeitUserDetails userDetails) {
        if (userDetails.getUserDto().id().equals(user.getId())) {
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
          return !sessions.isEmpty();
        }
      }
    }
    return false;
  }
}
