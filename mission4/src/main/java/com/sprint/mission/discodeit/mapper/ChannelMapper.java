package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {

  @Autowired
  protected UserMapper userMapper;

  @Autowired
  protected MessageRepository messageRepository;

  @Mapping(target = "participants", expression = "java(mapParticipants(channel))")
  @Mapping(target = "lastMessageAt", expression = "java(calculateLastMessageAt(channel))")
  public abstract ChannelDto toDto(Channel channel);

  protected List<UserDto> mapParticipants(Channel channel) {
    if (channel.getReadStatuses() == null) {
      return List.of();
    }

    return channel.getReadStatuses().stream()
        .map(status -> userMapper.toDto(status.getUser()))
        .toList();
  }

  protected Instant calculateLastMessageAt(Channel channel) {
    if (channel.getMessages() == null) {
      return null;
    }
    return messageRepository.findLastMessageAtByChannelId(channel.getId())
        .orElse(null);


  }
}

