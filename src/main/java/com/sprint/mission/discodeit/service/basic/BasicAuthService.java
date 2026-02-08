package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        Optional<User> byUsername = userRepository.findByUsername(request.username());
        if (byUsername.isEmpty()) {
            throw new IllegalArgumentException("username 또는 password가 일치하지 않습니다.");
        }
        User user = byUsername.get();
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("username 또는 password가 일치하지 않습니다.");
        }
        Optional<UserStatus> statusOpt = userStatusRepository.findByUserId(user.getId());
        UserStatus status = statusOpt.orElse(null);
        return UserResponse.from(user, status);
    }
}
