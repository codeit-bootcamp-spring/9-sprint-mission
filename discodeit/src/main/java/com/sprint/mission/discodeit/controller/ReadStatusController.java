package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping(
      path = "/create"
  )
  @Override
  public ResponseEntity<ReadStatus> create(
      @RequestBody ReadStatusCreateRequest request) {
    ReadStatus created = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }

  @PutMapping(
      path = "/update/{statusId}"
  )
  @Override
  public ResponseEntity<ReadStatus> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updated);
  }

  @GetMapping(
      path = "/user/{userId}")
  @Override
  public ResponseEntity<List<ReadStatus>> findAllByUserId(
      @PathVariable UUID userId) {
    List<ReadStatus> statuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(statuses);
  }
}