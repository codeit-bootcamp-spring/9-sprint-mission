package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Transactional(readOnly = true)
  @Override
  public UserDto login(LoginRequest loginRequest) {
    log.debug("로그인 시도: username={}", loginRequest.username());
    
    String username = loginRequest.username();
    String password = loginRequest.password();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> UserNotFoundException.withUsername(username));

    if (!user.getPassword().equals(password)) {
      throw InvalidCredentialsException.wrongPassword();
    }

    log.info("로그인 성공: userId={}, username={}", user.getId(), username);
    return userMapper.toDto(user);
  }

  public boolean isUserOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> (DiscodeitUserDetails) principal)
        .filter(userDetails -> userDetails.getUserDto().id().equals(userId))
        .anyMatch(userDetails -> !sessionRegistry.getAllSessions(userDetails, false).isEmpty());
  }
}
