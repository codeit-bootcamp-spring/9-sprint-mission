package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Profile("jcf")
@Primary
@RequiredArgsConstructor
public class JCFChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  public ChannelDto createPublic(PublicChannelCreateRequest request) {

    Channel channel = new Channel(
        request.name(),
        request.description(),
        ChannelType.PUBLIC
    );

    channelRepository.save(channel);
    List<User> users = userRepository.findAll();

    for (User user : users) {
      ReadStatus readStatus = new ReadStatus(user, channel);
      readStatusRepository.save(readStatus);
    }

    System.out.println(channelRepository.findAll());
    System.out.println(channelRepository.findAllByChannelType(ChannelType.PUBLIC));

    return channelMapper.toDto(
        channel,
        channel.getCreatedAt(),
        buildParticipants(channel)
    );
  }

  @Override
  @Transactional
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {

    Channel channel = new Channel(request.participantIds());
    channelRepository.save(channel);

    for (UUID userId : channel.getParticipantIds()) {

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("User not found"));

      ReadStatus readStatus = new ReadStatus(user, channel);

      readStatusRepository.save(readStatus);
    }

    return channelMapper.toDto(channel, null, buildParticipants(channel));
  }

  @Override
  public ChannelDto findById(UUID channelId) {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

    return channelMapper.toDto(
        channel,
        getLatestMessageTime(channelId),
        buildParticipants(channel)
    );
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {

    System.out.println("===== 채널 조회 시작 =====");
    System.out.println("userId = " + userId);

    List<Channel> channels = new ArrayList<>();

    channels.addAll(channelRepository.findAllByChannelType(ChannelType.PUBLIC));
    channels.addAll(channelRepository.findPrivateChannelsByUserId(userId));

    System.out.println("채널 개수 = " + channels.size());

    return channels.stream()
        .map(c -> channelMapper.toDto(
            c,
            getLatestMessageTime(c.getId()),
            buildParticipants(c)
        ))
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

    if (channel.getChannelType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
    }

    if (request.newName() != null && !request.newName().isBlank()) {
      channel.updateName(request.newName());
    }

    if (request.newDescription() != null) {
      channel.updateDescription(request.newDescription());
    }

    channelRepository.save(channel);

    return channelMapper.toDto(
        channel,
        getLatestMessageTime(channelId),
        buildParticipants(channel)
    );
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

    messageRepository.deleteByChannel_Id(channelId);
    readStatusRepository.deleteByChannel_Id(channelId);

    channelRepository.delete(channel);
  }

  private List<UserDto> buildParticipants(Channel channel) {

    if (channel.getChannelType() == ChannelType.PUBLIC) {

      return userRepository.findAll().stream()
          .map(user -> {

            UserStatus status = userStatusRepository
                .findByUser_Id(user.getId())
                .orElse(null);

            return userMapper.toDto(user, status != null && status.isOnline());

          })
          .toList();
    }

    return channel.getParticipantIds().stream()
        .map(id -> {

          User user = userRepository.findById(id)
              .orElseThrow();

          UserStatus status = userStatusRepository
              .findByUser_Id(id)
              .orElse(null);

          return userMapper.toDto(user, status != null && status.isOnline());

        }).toList();
  }

  private Instant getLatestMessageTime(UUID channelId) {

    return messageRepository
        .findLatestMessageTime(channelId)
        .orElse(
            channelRepository.findById(channelId)
                .map(Channel::getCreatedAt)
                .orElse(null)
        );
  }
}