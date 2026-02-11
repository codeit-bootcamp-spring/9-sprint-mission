package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest request) {

        return userRepository.findByUsername(request.username())
                .filter(user -> user.getPassword().equals(request.password()))
                .orElseThrow(() -> new NoSuchElementException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }
}

/*findByUsername 메소드를 호출해서 매개변수에 request.username을 넣고 반환된 user를 필터링을 통해 해당 유저의 비밀번호와
매개변수로 받은 request의 비밀번호가 같을시 true 아니면 오류를 반환한다
 */