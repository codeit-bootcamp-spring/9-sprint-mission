package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusView;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/status")
public class UserStatusController {

    private final UserStatusService userStatusService;

    @RequestMapping(value = "/online", method = RequestMethod.PUT)
    public UserStatusView updateOnline(
            @PathVariable UUID userId,
            @RequestBody(required = false) UserStatusUpdateRequest.Params params
    ) {
        return userStatusService.updateByUserId(userId, params);
    }
}