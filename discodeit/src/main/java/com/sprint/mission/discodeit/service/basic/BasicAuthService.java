package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserService userService;

  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    log.debug("로그인 시도: username = {}", loginRequest.username());
    User user = userRepository.findByUsername(username).orElseThrow(() -> {
      log.warn("로그인 실패 - 존재하지 않는 사용자: username = {}", username);
      return new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("username", username));
    });

    if (!user.getPassword().equals(password)) {
      log.warn("로그인 실패 - 비밀번호 불일치: username = {}, userId = {}", username, user.getId());
      throw new InvalidPasswordException(ErrorCode.INVALID_PASSWORD,
          Map.of("userId", user.getId()));
    }

    UserDto dto = userService.find(user.getId());
    log.info("로그인 성공: username = {}, userId = {}", username, user.getId());
    return dto;
  }
}
