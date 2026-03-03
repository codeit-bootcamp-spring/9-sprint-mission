package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;


  @Override
  public ResponseEntity<ReadStatus> createReadStatus(ReadStatusCreateRequest request) {

    ReadStatus created = readStatusService.create(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }


  @Override
  public ResponseEntity<ReadStatus> updateReadStatus(
      UUID readStatusId,
      ReadStatusUpdateRequest request
  ) {

    ReadStatus updated =
        readStatusService.update(readStatusId, request);

    return ResponseEntity.ok(updated);
  }


  @Override
  public ResponseEntity<List<ReadStatus>> getReadStatuses(UUID userId) {

    List<ReadStatus> list =
        readStatusService.findAllByUserId(userId);

    return ResponseEntity.ok(list);
  }
}