package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @GetMapping("/api/readStatuses")
  ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId);

  @Operation(summary = "Message 읽음 상태 생성")
  @PostMapping("/api/readStatuses")
  ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusCreateRequest request);

  @Operation(summary = "Message 읽음 상태 수정")
  @PatchMapping("/api/readStatuses/{readStatusId}")
  ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request);
}