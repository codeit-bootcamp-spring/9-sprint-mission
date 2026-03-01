package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discordit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discordit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatus create(ReadStatusCreateRequest request);

  ReadStatus find(UUID readStatusId);

  List<ReadStatus> findAllByUserId(UUID userId);

  ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request);

  void delete(UUID readStatusId);
}
