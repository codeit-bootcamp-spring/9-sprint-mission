package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusService userStatusService;

    @Override
    public UserDto.Response login(AuthDto.LoginRequest request) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(request.email()) && u.getPassword().equals(request.password()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        userStatusService.updateByUserId(user.getId());
        return new UserDto.Response(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                true,
                user.getProfileId(),
                user.getPhoneNumber()
        );
    }
}