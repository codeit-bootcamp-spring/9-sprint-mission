package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.AuthenticationException;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;


    @Transactional
    @Override
    public UserDto Login(LoginRequest request){
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(()->new AuthenticationException("Not Exist User - userName: " + request.username()));

        if (!user.getPassword().equals(request.password())){
            throw new AuthenticationException("Invalid password - userName: " + request.username());
        }
        return userMapper.toDto(user);
    }
}
