package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;
    @RequestMapping(
            path = "/api/login",
            method = RequestMethod.POST
    )
    public ResponseEntity<User> login(
            @RequestBody LoginRequest loginRequest
            ){
        User loginUser =authService.login(loginRequest);
        return ResponseEntity.ok(loginUser);

    }
}
