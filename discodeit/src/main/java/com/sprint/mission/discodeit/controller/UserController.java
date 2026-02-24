package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }
    // 전체 사용자 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }
    // 사용자 생성
    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@RequestBody UserCreateRequest request) {
        // 프로필은 일단 없음으로 처리
        return userService.create(request, Optional.<BinaryContentCreateRequest>empty());
    }
    // 단건 조회
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserDto getUser(@PathVariable UUID userId) {
        return userService.find(userId);
    }
    // 사용자 수정
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public UserDto updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequest request) {
        userService.update(userId, request, Optional.empty());
        return userService.find(userId);
    }
    // 사용자 삭제
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }
    // 온라인 상태 업데이트
    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public void updateUserStatus(@PathVariable UUID userId, @RequestBody UserStatusUpdateRequest request) {
        userStatusService.updateByUserId(userId, request);
    }

    @GetMapping("/api/user/findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }
}

