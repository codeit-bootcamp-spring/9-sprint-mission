package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Controller
public class AuthController {
    private final BasicAuthService basicAuthService;
    private final AuthService authService;

    @RequestMapping(
            path = "login"
    )
    public ResponseEntity login(
            @RequestPart("loginRequest") LoginRequest loginRequest
    ){


        User Dto = authService.login(loginRequest);
        return ResponseEntity.ok(Dto);
    }
}
