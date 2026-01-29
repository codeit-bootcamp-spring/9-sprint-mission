package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.ReadStatus;
import java.util.UUID;
import java.util.List;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    ReadStatus findById(UUID id);
    List<ReadStatus> findAll();
    void deleteById(UUID id);
}