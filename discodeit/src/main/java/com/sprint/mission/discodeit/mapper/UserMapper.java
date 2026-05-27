package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.springframework.security.core.session.SessionRegistry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final SessionRegistry sessionRegistry;

  public UserDto toDto(User user) {
    boolean online = sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> (DiscodeitUserDetails) principal)
        .anyMatch(userDetails -> userDetails.getUserDto().id().equals(user.getId()));

    BinaryContentDto profile = Optional.ofNullable(user.getProfile())
        .map(binaryContentMapper::toDto)
        .orElse(null);

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profile,
        online,
        user.getRole()
    );
  }
}