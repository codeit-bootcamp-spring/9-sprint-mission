package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth") // 인증 관련 API는 /api/auth 경로로 공통 설정
public class UserAuthController {

    private final UserService userService;

    // 사용자 로그인
    @RequestMapping(
            path = "/login",
            method = RequestMethod.POST
    )
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
        return userService.findAll().stream()
                .filter(u -> u.username().equals(loginRequest.username()))
                .findFirst()
                .map(user -> ResponseEntity.ok(user)) // 성공 시 200 OK + 유저정보
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()); // 실패 시 401
    }

    // 로그아웃 (선택 사항: 온라인 상태 업데이트와 연결 가능)
    @RequestMapping(
            path = "/logout",
            method = RequestMethod.POST)
    public ResponseEntity<Void> logout() {

        return ResponseEntity.noContent().build();
    }
}