package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online,
    Role role
) {

  public static UserDto from(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() == null ? null : new BinaryContentDto(
            user.getProfile().getId(),
            user.getProfile().getFileName(),
            user.getProfile().getSize(),
            user.getProfile().getContentType(),
            "http://localhost:8080/api/binaryContents/" + user.getProfile().getId() + "/download"),
        false,
        user.getRole()
    );
  }
}
