package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class LoginUserController {

    private final AuthService authService;
    private final UserStatusService userStatusService;

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST
    )
    public ResponseEntity<User> login(
            @RequestBody LoginRequest request
    ) {
        // 1️⃣ 인증
        User user = authService.login(request);

        // 2️⃣ 상태 갱신 (로그인 = 활동 시작)
        userStatusService.updateByUserId(
                user.getId(),
                new UserStatusUpdateRequest(Instant.now())
        );

        return ResponseEntity.ok(user);
    }
}

