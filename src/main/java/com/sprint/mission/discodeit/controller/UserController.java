package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST
    )
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserCreateRequest request
    ) {
        User createdUser = userService.create(request, Optional.empty());

        UserDto response = new UserDto(
                createdUser.getId(),
                createdUser.getCreatedAt(),
                createdUser.getUpdatedAt(),
                createdUser.getUsername(),
                createdUser.getEmail(),
                createdUser.getProfileId(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PUT
    )
    public ResponseEntity<User> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        User updatedUser = userService.update(
                userId,
                userUpdateRequest,
                Optional.empty() // 프로필 수정 없을 경우
        );

        return ResponseEntity.ok(updatedUser);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<UserDto> findUser(
            @PathVariable UUID userId
    ) {
        UserDto userDto = userService.find(userId);
        return ResponseEntity.ok(userDto);
    }

    @RequestMapping(
            value = "/findAll",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId
    ) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

}
