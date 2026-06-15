package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.JwtRegistry;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @CacheEvict(value = "users", allEntries = true)
  public UserDto updateRole(UUID userId, Role newRole) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));
    Role oldRole = user.getRole();
    user.updateRole(newRole);
    eventPublisher.publishEvent(new RoleUpdatedEvent(userId, oldRole, newRole));

    jwtRegistry.invalidateJwtInformationByUserId(userId);
    log.info("사용자 권한 변경으로 인한 토큰 강제 만료 처리 - userId: {}", userId);

    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    for (Object principal : allPrincipals) {
      if (principal instanceof DiscodeitUserDetails userDetails) {
        if (userDetails.getUserDto().id().equals(userId)) {
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
          for (SessionInformation session : sessions) {
            session.expireNow();
          }
        }
      }
    }
    return userMapper.toDto(user);
  }
}