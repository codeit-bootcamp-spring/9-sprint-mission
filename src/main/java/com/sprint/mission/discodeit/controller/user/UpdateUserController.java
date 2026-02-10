package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UpdateUserController {

    private final UserService userService;

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
}

