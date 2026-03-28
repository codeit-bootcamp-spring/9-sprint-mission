package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.request.RequestLoginDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;
import com.sprint.mission.discodeit.exepction.FailedLogin;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserState userState;

  public void login(RequestLoginDto requestDto) {
    /// 검증로직
    try {
      UUID id = userRepository.usernameToId(requestDto.username());

      if (!userRepository.checkInvalid(id, requestDto.password())) {
        userState.userState(requestDto.username(), id);
        userStatusRepository.update(
            new RequestUpdateUserStatusDto(id, requestDto.username(), null));
      } else {
        throw new Exception();
      }
    } catch (Exception ignore) {
      throw new FailedLogin("Invalid username or password");
    }
  }

  public void logout() {
    userStatusRepository.update(
        new RequestUpdateUserStatusDto(userState.getUserId(), userState.getUsername(), null));
    userState.userState("");
  }
}
