package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 읽음 상태 API 컨트롤러.
 * 사용자별 채널 읽음 상태 생성, 수정, 조회를 처리한다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping
  @Override
  public ResponseEntity<ReadStatusDto> create(
      @Valid @RequestBody ReadStatusCreateRequest request) {
    ReadStatusDto readStatusDto = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusDto);
  }

  @PatchMapping(path = "{readStatusId}")
  @Override
  public ResponseEntity<ReadStatusDto> update(@PathVariable("readStatusId") UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request) {
    ReadStatusDto readStatusDto = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(readStatusDto);
  }

  @GetMapping
  @Override
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }
}
