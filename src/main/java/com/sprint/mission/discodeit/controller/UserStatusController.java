package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/status")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.PATCH, path = "/touch")
    public UserStatusResponse touch(
            @PathVariable UUID userId
    ) {
        return userStatusService.touchByUserId(userId);
    }
}
