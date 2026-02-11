package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ReadStatus create(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatus update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }

    @GetMapping("/user/{userId}")
    public List<ReadStatus> findByUserId(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}

