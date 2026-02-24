package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @Override
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(UUID userId) {
    List<ReadStatusResponse> responses = readStatusService.findAllByUserId(userId).stream()
        .map(this::convertToResponse).toList();
    return ResponseEntity.ok(responses);
  }

  @Override
  public ResponseEntity<ReadStatusResponse> create(@Valid ReadStatusCreateRequest request) {
    ReadStatus rs = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(rs));
  }

  @Override
  public ResponseEntity<ReadStatusResponse> update(
      @PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatus rs = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(convertToResponse(rs));
  }

  private ReadStatusResponse convertToResponse(ReadStatus rs) {
    return new ReadStatusResponse(rs.getId(), rs.getCreatedAt(), rs.getUpdatedAt(), rs.getUserId(),
        rs.getChannelId(), rs.getLastReadAt());
  }
}