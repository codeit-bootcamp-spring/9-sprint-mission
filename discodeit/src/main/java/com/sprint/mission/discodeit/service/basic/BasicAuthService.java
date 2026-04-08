package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.LoginFailedException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    log.debug("로그인 시도, username={}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("사용자를 찾을 수 없음, username={}", username);
          return new LoginFailedException();
        });

    if (!user.getPassword().equals(password)) {
      log.error("잘못된 비밀번호로 로그인 시도, username={}", username);
      throw new LoginFailedException();
    }

    log.info("로그인 성공, username={}", username);
    return userMapper.toDto(user);
  }
}