package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    타입은 METHOD / RetentionPolicy는 RUNTIME 아마 시간 체크 용도로 쓰는 것 같음
    @PostMapping
    public void createUser(@RequestBody UserCreateRequestDto requestDto) {
        userService.create(requestDto);
    }
}
