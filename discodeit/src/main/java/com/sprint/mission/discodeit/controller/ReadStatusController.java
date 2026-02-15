package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 생성
    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} // 멀티파트 설정 추가
    )
    public ResponseEntity<ReadStatus> create(
            @RequestPart("readStatusCreateRequest") ReadStatusCreateRequest request) {
        ReadStatus created = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(
            path = "/update/{statusId}",
            method = RequestMethod.PUT,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} // 멀티파트 설정 추가
    )
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID statusId,
            @RequestPart("readStatusUpdateRequest") ReadStatusUpdateRequest request // RequestPart로 변경
    ) {
        ReadStatus updated = readStatusService.update(statusId, request);
        return ResponseEntity.ok(updated);
    }

    // 특정 사용자 조회
    @RequestMapping(
            path = "/user/{userId}",
            method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable UUID userId) {
        // [교정] static 호출 에러 해결
        List<ReadStatus> statuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(statuses);
    }
}