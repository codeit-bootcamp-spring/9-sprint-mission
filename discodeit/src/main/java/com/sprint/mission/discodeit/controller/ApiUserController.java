package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.UserDto;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class ApiUserController {

    private final UserService userService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserView> users = userService.findAll();
        List<UserDto> result = users.stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    private UserDto toDto(UserView v) {
        return new UserDto(
                v.id(),
                v.createdAt(),
                v.updatedAt(),
                v.username(),
                v.email(),
                v.profileImageId(),
                v.online()
        );
    }
}