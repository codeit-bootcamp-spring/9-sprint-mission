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
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

  @Mapping(target = "lastMessageAt", source = "channel", qualifiedByName = "mapLastMessageAt")
  @Mapping(target = "participantIds", source = "channel", qualifiedByName = "mapParticipantIds")
  ChannelDto toDto(Channel channel);

  @Named("mapLastMessageAt")
  default Instant mapLastMessageAt(Channel channel) {
    if (channel.getReadStatuses() == null || channel.getReadStatuses().isEmpty()) {
      return Instant.EPOCH;
    }

    return channel.getReadStatuses().stream()
        .map(ReadStatus::getLastReadAt)
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .orElse(Instant.EPOCH);
  }

  @Named("mapParticipantIds")
  default List<UUID> mapParticipantIds(Channel channel) {
    if (channel.getType() != ChannelType.PRIVATE || channel.getReadStatuses() == null) {
      return Collections.emptyList();
    }

    return channel.getReadStatuses().stream()
        .map(rs -> rs.getUser().getId())
        .toList();
  }
}
