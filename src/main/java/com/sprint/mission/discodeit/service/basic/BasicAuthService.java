package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public UserDto login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "User with username " + request.username() + " not found"
                )
            );

        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("Wrong password");
        }

        boolean online = userStatusRepository.findByUser_Id(user.getId())
            .map(UserStatus::isOnline)
            .orElse(false);

        return userMapper.toDto(user, online);
    }
}