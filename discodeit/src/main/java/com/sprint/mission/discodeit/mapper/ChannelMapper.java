package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  public ChannelDto toDto(Channel channel) {
    if (channel == null) {
      return null;
    }

    Instant lastMessageAt = messageRepository.findAllByChannel_Id(channel.getId()).stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(Instant.MIN);

    List<UUID> participantIds = readStatusRepository.findAllByChannel_Id(channel.getId())
        .stream()
        .map(ReadStatus::getUser)
        .map(BaseEntity::getId)
        .toList();

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participantIds,
        lastMessageAt
    );
  }
}
