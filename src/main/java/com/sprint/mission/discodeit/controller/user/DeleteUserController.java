package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class DeleteUserController {

    private final UserService userService;

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
