package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final ReadStatusRepository readStatusRepository;

  public ChannelDto toDto(Channel channel){
    Optional<Instant> lastMessageTime = readStatusRepository.findOldestReadAtByChannelId(channel.getId());
    List<UUID> participantIds = readStatusRepository.findAllByChannel(channel)
        .stream()
        .map(BaseEntity::getId)
        .toList();

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        (channel.getType() == ChannelType.PRIVATE) ? participantIds : Collections.emptyList(),
        lastMessageTime.orElse(Instant.EPOCH)
    );
  }
}
