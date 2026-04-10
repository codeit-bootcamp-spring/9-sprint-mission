package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @Override
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(UUID userId) {
    log.debug("Fetching read statuses for user: {}", userId);
    return ResponseEntity.ok(readStatusService.getOrCreateReadStatus(userId));
  }

  @Override
  public ResponseEntity<ReadStatusDto> create(@Valid ReadStatusCreateRequest request) {
    log.info("Creating read status for user: {} in channel: {}", request.userId(),
        request.channelId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readStatusService.create(request));
  }

  @Override
  public ResponseEntity<ReadStatusDto> update(@PathVariable UUID readStatusId,
      @Valid ReadStatusUpdateRequest request) {
    log.info("Updating read status: {} with time: {}", readStatusId, request.newLastReadAt());
    return ResponseEntity.ok(readStatusService.update(readStatusId, request));
  }
}