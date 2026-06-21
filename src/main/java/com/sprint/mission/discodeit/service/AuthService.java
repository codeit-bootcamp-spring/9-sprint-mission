package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;

public interface AuthService {

  UserDto updateRole(RoleUpdateRequest request);

  UserDto updateRoleInternal(RoleUpdateRequest request);

  JwtInformation refreshToken(String refreshToken);
}
