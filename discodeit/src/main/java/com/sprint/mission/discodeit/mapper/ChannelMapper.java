package com.sprint.mission.discodeit.mapper;


import static java.util.stream.Collectors.toList;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final ReadStatusRepository readStatusRepository;

  public ChannelDto toDto(Channel channel){
    List<ReadStatus> statuses = channel.getReadStatuses();

    Instant lastMessageTime = statuses.stream()
        .map(ReadStatus::getLastReadAt)
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .orElse(Instant.EPOCH);

    List<UUID> participantIds = statuses
        .stream()
        .map(readStatus -> {
          return readStatus.getUser().getId();
        })
        .toList();

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        (channel.getType() == ChannelType.PRIVATE) ? participantIds : Collections.emptyList(),
        lastMessageTime
    );
  }
}
