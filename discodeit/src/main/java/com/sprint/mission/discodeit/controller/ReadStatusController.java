package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;
  @Override
  @PostMapping
  public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatusDto createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @PatchMapping("/{readStatusId}") // 💡 프론트엔드 호출 경로와 일치 ({UUID} 부분)
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable("readStatusId") UUID readStatusId, // 💡 이름을 명확히 지정
      @RequestBody(required = false) ReadStatusUpdateRequest request // 💡 null 방지용 required=false
  ) {
    // 만약 request가 null로 들어오면 기본값(현재시간)으로 처리하는 로직을 서비스에 추가하면 안전합니다.
    ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updatedReadStatus);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
    List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}
