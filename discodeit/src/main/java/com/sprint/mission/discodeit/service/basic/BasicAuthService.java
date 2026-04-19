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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;


    @Transactional
    @Override
    public UserDto Login(LoginRequest request){
        log.info("로그인 시도: username={}", request.username());
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(()->{
                    log.warn("로그인 실패: 존재하지 않는 사용자 - username={}", request.username());
                    return new AuthenticationException("Not Exist User - userName: " + request.username());
                });

        if (!user.getPassword().equals(request.password())){
            log.warn("로그인 실패: 비밀번호 불일치 - username={}", request.username());
            throw new AuthenticationException("Invalid password - userName: " + request.username());
        }

        log.debug("로그인 성공: username={}, userId={}", user.getUsername(), user.getId());
        return userMapper.toDto(user);
    }
}
