package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    log.debug("loadUserByUsername 호출됨: username={}", username);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("사용자를 찾을 수 없음: {}", username);
          return new UsernameNotFoundException("User not found: " + username);
        });

    UserDto userDto = userMapper.toDto(user, true);
    return new DiscodeitUserDetails(userDto, user.getPassword());
  }
}
