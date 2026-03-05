package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/userStatus")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    @PatchMapping
    public UserStatusDto updateUserStatusByUserId(
        @PathVariable UUID userId,
        @RequestBody UserStatusUpdateRequest request) {

        return userStatusService.updateStatus(userId, request);
    }
}