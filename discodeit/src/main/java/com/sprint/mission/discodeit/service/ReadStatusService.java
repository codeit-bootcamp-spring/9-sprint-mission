package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusDto.CreateRequest request);
    void update(UUID id);
}