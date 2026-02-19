package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusView;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusView create(ReadStatusCreateRequest request);
    ReadStatusView findById(UUID readStatusId);
    List<ReadStatusView> findAllByUserId(UUID userId);
    ReadStatusView update(ReadStatusUpdateRequest request);
    void delete(UUID readStatusId);
    boolean existsById(UUID readStatusId);
}