package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.AuthenticationException;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public User Login(LoginRequest request){
        User user = userRepository.findByUserName(request.username())
                .orElseThrow(()->new AuthenticationException("Not Exist User - userName: " + request.username()));

        if (!user.getPassword().equals(request.password())){
            throw new AuthenticationException("Invalid password - userName: " + request.username());
        }
        return user;
    }
}
