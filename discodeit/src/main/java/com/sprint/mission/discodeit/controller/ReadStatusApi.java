package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @GetMapping
  ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam(name = "userId") UUID userId);

  @Operation(summary = "Message 읽음 상태 생성")
  @PostMapping
  ResponseEntity<ReadStatusDto> create(@Valid @RequestBody ReadStatusCreateRequest request);

  @Operation(summary = "Message 읽음 상태 수정")
  @PatchMapping("/{readStatusId}")
  ResponseEntity<ReadStatusDto> update(
      @PathVariable(name = "readStatusId") UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request
  );
}