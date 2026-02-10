package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class CreateUserController {

    private final UserService userService;

    @RequestMapping(
            value = "/create",
            method = org.springframework.web.bind.annotation.RequestMethod.POST
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
}
