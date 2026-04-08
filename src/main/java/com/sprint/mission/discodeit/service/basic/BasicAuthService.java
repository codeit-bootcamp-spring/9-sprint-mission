package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스 구현체.
 * 사용자명과 비밀번호 기반의 로그인을 처리한다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    log.info("로그인 시도: username={}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("로그인 실패 - 사용자 없음: username={}", username);
          return new InvalidPasswordException(Map.of("username", username));
        });

    if (!user.getPassword().equals(password)) {
      log.warn("로그인 실패 - 비밀번호 불일치: username={}", username);
      throw new InvalidPasswordException(Map.of("username", username));
    }

    log.info("로그인 성공: userId={}", user.getId());
    return userMapper.toDto(user);
  }
}
