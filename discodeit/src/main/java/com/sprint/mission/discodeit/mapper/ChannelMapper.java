package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final UserMapper userMapper;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  public ChannelDto toDto(Channel entity) {
    if (entity == null) {
      return null;
    }
    Instant lastMessageAt = messageRepository.findAllByChannelId(entity.getId())
        .stream()
        .max(Comparator.comparing(Message::getCreatedAt))
        .map(msg -> msg.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())
        .orElse(null);

    List<UserDto> participants = readStatusRepository.findAllByChannelId(entity.getId())
        .stream()
        .map(readStatus -> userRepository.findById(readStatus.getUserId()).orElse(null))
        .filter(java.util.Objects::nonNull)
        .map(user -> {
          try {
            return userMapper.toDto(user);
          } catch (jakarta.persistence.EntityNotFoundException e) {

            return null;
          }
        })
        .filter(java.util.Objects::nonNull)
        .toList();

    return new ChannelDto(
        entity.getId(),
        entity.getType(),
        entity.getName(),
        entity.getDescription(),
        participants,
        lastMessageAt
    );
  }

}