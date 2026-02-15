package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    //유저생성
    @RequestMapping(method = RequestMethod.POST)
    public User create(@RequestBody UserCreateRequest request) {
        return userService.create(request, Optional.empty());
    }


    //유저 단건 조회
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserDto find(@PathVariable UUID userId) {
        return userService.find(userId);
    }

    //유저 다건 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    //유저 수정
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public User update(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        return userService.update(
                userId,
                userUpdateRequest,Optional.empty()
        );
    }

    //유저 삭제
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @PatchMapping("/users/{userId}/status")
    public UserStatus updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        return userStatusService.updateByUserId(userId, request);
    }

}