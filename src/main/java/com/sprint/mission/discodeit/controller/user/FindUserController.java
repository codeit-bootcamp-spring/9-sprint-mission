package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class FindUserController {

    private final UserService userService;

    @RequestMapping(
            value = "/{userId}",
            method = org.springframework.web.bind.annotation.RequestMethod.GET
    )
    public ResponseEntity<UserDto> findUser(
            @PathVariable UUID userId
    ) {
        UserDto userDto = userService.find(userId);
        return ResponseEntity.ok(userDto);
    }
}
