package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserView login(LoginRequest request) {
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }

        // 1) 유저 찾기
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("invalid username or password"));

        // 2) 비밀번호 검증
        if (user.getPassword() == null || !user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("invalid username or password");
        }

        // 3) 로그인 성공 → UserStatus 갱신
        UserStatus status = userStatusRepository.findByUserId(user.getId()).orElse(null);

        if (status != null) {
            status.touch(Instant.now());
            userStatusRepository.save(status);
        }

        boolean online = status != null && status.isOnlineNow();
        Instant lastSeenAt = status != null ? status.getLastSeenAt() : null;

        // 4) UserView 반환 (password 제외)
        return new UserView(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImageId(),
                online,
                lastSeenAt
        );
    }
}