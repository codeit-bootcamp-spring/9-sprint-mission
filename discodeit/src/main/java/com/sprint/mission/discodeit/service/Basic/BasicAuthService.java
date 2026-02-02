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
    private UserRepository userRepository;

    public User Login(LoginRequest request){
        User user = null;
        user = userRepository.findByUserName(request.userName());

        if (user == null){
            throw new AuthenticationException("Not Exist User - userName: " + request.userName());
        }

        if (!user.getPassword().equals(request.password())){
            throw new AuthenticationException("Invalid passward - userName: " + request.userName());
        }

        return user;
    }
}
