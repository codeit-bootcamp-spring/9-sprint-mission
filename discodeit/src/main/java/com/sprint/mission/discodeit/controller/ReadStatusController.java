package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    // 메시지 수신 정보 생성
    @PostMapping("/read-statuses")
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    // 메시지 수신 정보 수정
    @PutMapping("/read-statuses/{readStatusId}")
    public ReadStatus updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @GetMapping("/users/{userId}/read-statuses")
    public List<ReadStatus> getReadStatusesByUser(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}


