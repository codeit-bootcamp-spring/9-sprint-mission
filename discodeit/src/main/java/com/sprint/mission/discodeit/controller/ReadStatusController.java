package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto; // 🚩 DTO 임포트 추가
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping
  @Override // 🚩 인터페이스 메서드임을 명시
  public ResponseEntity<ReadStatusDto> create(
      @RequestBody ReadStatusCreateRequest request) { // 🚩 리턴 타입 변경
    ReadStatusDto createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @PatchMapping(path = "{readStatusId}")
  @Override
  public ResponseEntity<ReadStatusDto> update(@PathVariable UUID readStatusId,
      // 🚩 redundant name 제거
      @RequestBody ReadStatusUpdateRequest request) { // 🚩 리턴 타입 변경
    ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }

  @GetMapping
  @Override
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @RequestParam UUID userId) { // 🚩 리턴 타입 변경
    List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}