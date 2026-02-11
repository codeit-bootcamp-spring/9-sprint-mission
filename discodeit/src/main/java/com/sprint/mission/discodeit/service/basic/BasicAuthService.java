package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.AuthLoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        // 지금 Repository에 "findByLoginId" 같은 메서드가 없어서
        // findAll()로 찾아서 로그인 처리(요구사항 충족용으로 단순 구현)
        for (User u : userRepository.findAll()) {

            // 요구사항이 username이라고 써있을 수 있어서 username 기준을 1순위로,
            // 그래도 현실적으로 loginId로도 로그인 가능하게 안전 처리
            boolean matchUsername = request.username() != null && request.username().equals(u.getUsername());
            boolean matchLoginId = request.username() != null && request.username().equals(u.getLoginId());

            if ((matchUsername || matchLoginId) && request.password() != null && request.password().equals(u.getPassword())) {
                return new AuthLoginResponse(u.getId(), u.getUsername(), u.getNickname());
            }
        }

        throw new IllegalArgumentException("로그인 실패: username/password가 올바르지 않습니다.");
    }
}

