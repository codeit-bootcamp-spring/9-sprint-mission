package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
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

  @Override
  @PostMapping
  public ResponseEntity<ReadStatusResponse> create(@Valid @RequestBody ReadStatusCreateRequest request) {
    ReadStatusResponse createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @Override
  @PatchMapping(path = "/{readStatusId}")
  public ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request) {
    ReadStatusResponse updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam("userId") UUID userId) {
    List<ReadStatusResponse> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}
