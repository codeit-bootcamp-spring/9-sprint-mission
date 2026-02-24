package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final AuthService authService;

    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public ResponseEntity<UserResponse> Login(@RequestBody LoginRequest request){

        User user = authService.Login(request);

        UserStatus userStatus = userStatusService.find(user.getUserStateId());

        UserResponse userResponse = new UserResponse(user.getId()
                , user.getName()
                , user.getEmail()
                , user.getProfileId()
                , userStatus.getLastActiveAt()
                , userStatus.checkIsLogin()
        );

        return ResponseEntity.ok(userResponse);
    }
}
