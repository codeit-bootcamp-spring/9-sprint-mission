package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserState userState;

    public void login(LoginDto requestDto) {
        /// 검증로직
        try {
            UUID id = userRepository.userNameToId(requestDto.name());

            if(!userRepository.checkInvalid(id, requestDto.password())) {
                userState.userState(requestDto.name(), id);
            } else throw new Exception();
        } catch (Exception ignore) {
            throw new NotFound("해당 사용자를 찾지 못했습니다.");
        }
    }

    public void logout() {
        userState.userState("");
    }
}
