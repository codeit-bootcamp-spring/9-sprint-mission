package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DTO.LoginRequest;
import com.sprint.mission.discodeit.service.DTO.UserResponse;
import com.sprint.mission.discodeit.status.UserStatusInterface;
import com.sprint.mission.discodeit.status.add.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    //private final UserStatusInterface userStatusInterface;

    public UserResponse login(LoginRequest request) {
        User user = userRepository
                .findByUsernameAndPassword(
                        request.username(),
                        request.password()
                )
                .orElseThrow(() -> new RuntimeException("로그인 실패"));

//        UserStatus status = userStatusInterface
//                .findByUser(user.getId())
//                .orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                false
                //status.online()
        );
    }
}