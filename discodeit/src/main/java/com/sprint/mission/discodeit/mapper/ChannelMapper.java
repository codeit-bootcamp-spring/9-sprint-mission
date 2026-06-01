package com.sprint.mission.discodeit.mapper;


import static java.util.stream.Collectors.toList;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import java.time.Instant;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring",  uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;
  @Autowired
  private UserMapper userMapper;

  @Mapping(target = "lastMessageAt", source = "channel", qualifiedByName = "mapLastMessageAt")
  @Mapping(target = "participants", source = "channel", qualifiedByName = "mapParticipants")
  abstract public ChannelDto toDto(Channel channel);

  @Named("mapLastMessageAt")
  protected Instant mapLastMessageAt(Channel channel) {
    if (channel.getReadStatuses() == null || channel.getReadStatuses().isEmpty()) {
      return Instant.EPOCH;
    }

    return channel.getReadStatuses().stream()
        .map(ReadStatus::getLastReadAt)
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .orElse(Instant.EPOCH);
  }

  @Named("mapParticipants")
  protected List<UserDto> mapParticipants(Channel channel) {
    List<UserDto> participants = new ArrayList<>();
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      readStatusRepository.findAllByChannelId(channel.getId())
          .stream()
          .map(ReadStatus::getUser)
          .map(userMapper::toDto)
          .forEach(participants::add);
    }
    return participants;
  }
}
