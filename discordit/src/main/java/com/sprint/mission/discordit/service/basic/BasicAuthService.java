package com.sprint.mission.discordit.service.basic;

import com.sprint.mission.discordit.dto.data.UserDto;
import com.sprint.mission.discordit.dto.request.LoginRequest;
import com.sprint.mission.discordit.entity.User;
import com.sprint.mission.discordit.repository.UserRepository;
import com.sprint.mission.discordit.service.AuthService;
import com.sprint.mission.discordit.service.UserService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserService userService;

  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    User user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new NoSuchElementException("User with username " + username + " not found"));

    if (!user.getPassword().equals(password)) {
      throw new IllegalArgumentException("Wrong password");
    }

    return userService.find(user.getId());
  }
}
