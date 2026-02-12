package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user") // 기본 경로 설정
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // [1] 모든 사용자 조회 (script.js 연동)
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // [2] 사용자 등록
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<UserDto> register(@RequestBody UserCreateRequest request) {
        return userService.create(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    // [3] 사용자 정보 수정
    @RequestMapping(value = "/update", method = RequestMethod.POST) // 혹은 PATCH
    public ResponseEntity<UserDto> update(@RequestParam UUID id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // [4] 사용자 삭제
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID id) {
        if (userService.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // [5] 사용자 온라인 상태 업데이트
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public ResponseEntity<Void> updateStatus(@RequestParam UUID userId) {
        userStatusService.updateByUserId(userId);
        return ResponseEntity.ok().build();
    }
}