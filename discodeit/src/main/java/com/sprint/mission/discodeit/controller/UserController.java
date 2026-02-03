package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    //    타입은 METHOD / RetentionPolicy는 RUNTIME 아마 시간 체크 용도로 쓰는 것 같음
    //    Post = 유저 생성
    @PostMapping
    public UserResponseDto createUser(@RequestBody UserCreateRequestDto request) {

        return userService.create(request);
    }
    //    GET = 유저 단건 조회 {userId}
    @GetMapping("/{userId}")
    public UserResponseDto findUser(@PathVariable UUID userId) {
        return userService.find(userId);
    }
    //    GET = 유저 다건 조회 (users)
    @GetMapping("/users")
    public List<UserResponseDto> findAllUsers() {
        return userService.findAll();
    }

    @PutMapping("/{userId}")
    public UserResponseDto updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequestDto request
    ) {
        return userService.update(userId, request);
    }

    @PutMapping("/users")
    public void deleteUser(@PathVariable UUID userId)
    {userService.delete(userId);}

}

