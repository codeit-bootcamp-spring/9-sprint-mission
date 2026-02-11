package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.Exception.AuthenticationException;
import com.sprint.mission.discodeit.DTO.AuthService.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    public User Login(LoginRequest request){
        User user = userRepository.findByUserName(request.userName())
                .orElseThrow(()->new AuthenticationException("Not Exist User - userName: " + request.userName()));

        if (!user.getPassword().equals(request.password())){
            throw new AuthenticationException("Invalid password - userName: " + request.userName());
        }

        return user;
    }
}
