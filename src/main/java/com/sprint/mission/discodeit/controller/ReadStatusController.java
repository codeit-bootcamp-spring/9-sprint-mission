package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @GetMapping
    public List<ReadStatusResponse> findAllByUserId(
        @RequestParam UUID userId
    ) {
        return readStatusService.findAllByUserId(userId);
    }

    @GetMapping("/{readStatusId}")
    public ReadStatusResponse findById(
        @PathVariable UUID readStatusId
    ) {
        return readStatusService.findById(readStatusId);
    }

    @PostMapping
    public ReadStatusResponse create(
        @RequestBody ReadStatusCreateRequest request
    ) {
        return readStatusService.create(request);
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatusResponse update(
        @PathVariable UUID readStatusId,
        @RequestBody ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }
}