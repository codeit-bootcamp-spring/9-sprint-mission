package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/read-status")
public class UserReadStatusController {

    private final ReadStatusService readStatusService;

    public UserReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @GetMapping("/{readStatusId}")
    public ReadStatusResponse get(@PathVariable UUID readStatusId) {
        return readStatusService.findById(readStatusId);
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatusResponse update(@PathVariable UUID readStatusId) {
        return readStatusService.markAsReadById(readStatusId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusResponse> getAll(
            @PathVariable UUID userId
    ) {
        return readStatusService.findAllByUserId(userId);
    }
}
