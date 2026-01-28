package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor // 생성자 누락 오류 수정
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserDto.Response login(AuthDto.LoginRequest request) {
        User foundUser = null;
        List<User> users = userRepository.findAll();

        for (User user : users) {
            // request.username() -> request.email()로 수정
            if (user.getEmail().equals(request.email()) && // 로그인 시 이메일을 기준으로 비교
                    user.getPassword().equals(request.password())) {
                foundUser = user;
                break;
            }
        }

        if (foundUser == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return new UserDto.Response(
                foundUser.getId(),
                foundUser.getDisplayName(),
                foundUser.getEmail(),
                true,
                foundUser.getProfileId()
        );
    }
}