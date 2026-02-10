package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.Instant;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UpdateUserStatusController {

    private final UserStatusService userStatusService;

    @RequestMapping(
            value = "/{userId}/status",
            method = RequestMethod.PUT
    )
    public ResponseEntity<UserStatus> updateStatus(
            @PathVariable UUID userId
    ) {
        UserStatusUpdateRequest request =
                new UserStatusUpdateRequest(Instant.now());

        UserStatus updatedStatus =
                userStatusService.updateByUserId(userId, request);

        return ResponseEntity.ok(updatedStatus);
    }
}

