package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusDto toDto(ReadStatus entity) {
    return new ReadStatusDto(
        entity.getId(),
        entity.getUserId(),
        entity.getChannelId(),
        entity.getLastReadAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }
}