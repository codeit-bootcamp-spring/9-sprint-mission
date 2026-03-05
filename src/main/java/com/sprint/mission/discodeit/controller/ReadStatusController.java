package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.HttpStatus;
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
    public List<ReadStatusDto> findAllByUserId(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadStatusDto create(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatusDto update(
        @PathVariable UUID readStatusId,
        @RequestBody ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }
}