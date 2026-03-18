package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;
  private final ReadStatusMapper readStatusMapper;

  @PostMapping
  @Override
  public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest request) {
    //서비스를 통해 엔티티 생성
    ReadStatus createdReadStatus = readStatusService.create(request);

    //생성된 엔티티를 DTO로 변환해서 반환, 엔티티 직접 내보내는건 위험
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(readStatusMapper.toDto(createdReadStatus));
  }

  @PatchMapping("/{readStatusId}")
  @Override
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatusMapper.toDto(updatedReadStatus));
  }

  @GetMapping
  @Override
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

    List<ReadStatusDto> dtos = readStatuses.stream()
        .map(readStatusMapper::toDto)
        .toList();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(dtos);
  }
}
