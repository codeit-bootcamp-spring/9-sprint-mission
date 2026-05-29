package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionManager sessionManager;
  private final com.sprint.mission.discodeit.security.jwt.store.JwtRegistry jwtRegistry;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(RoleUpdateRequest request) {
    return updateRoleInternal(request);
  }

  @Transactional
  @Override
  public UserDto updateRoleInternal(RoleUpdateRequest request) {
    UUID userId = request.userId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Role newRole = request.newRole();
    user.updateRole(newRole);

    // invalidate session manager entries (if any) and JWT registry entries
    sessionManager.invalidateSessionsByUserId(userId);
    jwtRegistry.invalidateJwtInformationByUserId(userId);

    return userMapper.toDto(user);
  }


}
