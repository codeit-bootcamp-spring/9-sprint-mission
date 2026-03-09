package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  public UserDto toDto(User user) {
    Boolean online = user.getStatus() != null && user.getStatus().isOnline();
    UUID profileId = user.getProfile() != null ? user.getProfile().getId() : null;

    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        profileId,
        online
    );
  }
}