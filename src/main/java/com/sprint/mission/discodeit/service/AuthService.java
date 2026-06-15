package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public interface AuthService {
  UserDto updateRole(UUID userId, Role newRole);
}
