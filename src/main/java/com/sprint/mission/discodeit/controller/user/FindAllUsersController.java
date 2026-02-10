package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class FindAllUsersController {

    private final UserService userService;

    @RequestMapping(
            method = org.springframework.web.bind.annotation.RequestMethod.GET
    )
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
}

