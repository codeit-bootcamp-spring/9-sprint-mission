package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserStatusController {

    private final UserStatusService userStatusService;

    @PostMapping("/{userId}/login")
    public UserStatus login(@PathVariable UUID userId) {

        UserStatusUpdateRequest request =
                new UserStatusUpdateRequest(Instant.now());

        return userStatusService.updateByUserId(userId, request);
    }
}