package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusDto toDto(ReadStatus entity) {
    if (entity == null) {
      return null;
    }
    return new ReadStatusDto(
        entity.getId(),
        entity.getUser().getId(),
        entity.getChannel().getId(),
        entity.getLastReadAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

}
