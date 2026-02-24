package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping("/channels/{channelId}/read-status")
    public ResponseEntity<ReadStatus> createReadStatus(
            @PathVariable UUID channelId,
            @RequestBody ReadStatusCreateRequest readStatusCreateRequest)
    {
        ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.ok(readStatus);
    }

    @PatchMapping("/read-status/{readStatusId}")
    public ResponseEntity<ReadStatus> updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatus readStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.ok(readStatus);
    }

    @GetMapping("/users/{userId}/read-status")
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatuses);
    }
}
