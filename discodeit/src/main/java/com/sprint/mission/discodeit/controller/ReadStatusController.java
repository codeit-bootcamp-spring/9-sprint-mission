package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // [1] 특정 채널의 메시지 수신 정보 생성
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusRequest request) {
        return ResponseEntity.ok(readStatusService.create(request));
    }

    // [2] 특정 채널의 메시지 수신 정보 수정 (읽은 시간 갱신)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<Void> update(@RequestParam java.util.UUID id) {
        readStatusService.update(id);
        return ResponseEntity.ok().build();
    }
}