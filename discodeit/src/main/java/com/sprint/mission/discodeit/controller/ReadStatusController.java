package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

//    수신 정보 생성
    @PostMapping
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatus created = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
//    수신 정보 수정
    @PutMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatus updated = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(updated);
    }
//    수신 정보 조회
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUser(
            @RequestParam UUID userId
    ) {
        List<ReadStatus> list = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(list);
    }
}
