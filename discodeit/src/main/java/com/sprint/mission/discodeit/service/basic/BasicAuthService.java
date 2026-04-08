package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.WrongPasswordException;
import com.sprint.mission.discodeit.exception.user.UserUsernameNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDto login(LoginRequest request) {
    log.info("Attempting login for username: {}", request.username());

    User user = userRepository.findByUsername(request.username())
        .orElseThrow(() -> {
          log.warn("Login failed: Username '{}' not found", request.username());
          return new UserUsernameNotFoundException(request.username());
        });

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      log.warn("Login failed: Wrong password for user '{}'", request.username());
      throw new WrongPasswordException();
    }

    log.info("Login successful: user id '{}'", user.getId());
    return userMapper.toDto(user);
  }
}