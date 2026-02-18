package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/api/auth/login",method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        User user = authService.login(request);
        userStatusService.updateByUserId(user.getId(), new UserStatusUpdateRequest(Instant.now()));
        return ResponseEntity.ok(user);
    }
}
