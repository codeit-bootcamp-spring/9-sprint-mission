package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
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
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping("createUser")
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest userCreateRequest)
    {
        User user = userService.create(userCreateRequest, Optional.empty());
        return ResponseEntity.ok(user);
    }

    @PatchMapping("updateUser")
    public ResponseEntity<User> updateUser(@PathVariable UUID userId,
                                           @RequestBody UserUpdateRequest userUpdateRequest)
    {
        User user = userService.update(userId, userUpdateRequest, Optional.empty());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("deleteUser")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId)
    {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("findUser")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("userStatus")
    public ResponseEntity<UserStatus> userStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatus user = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.ok(user);
    }

}
