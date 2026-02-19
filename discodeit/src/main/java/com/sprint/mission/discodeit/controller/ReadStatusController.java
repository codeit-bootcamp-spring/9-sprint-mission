package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/readststus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    // [ ] 특정 채널의 메시지 수신 정보를 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readStatusService.create(request));
    }


    // [ ] 특정 채널의 메시지 수신 정보를 수정
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    // [ ] 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}

