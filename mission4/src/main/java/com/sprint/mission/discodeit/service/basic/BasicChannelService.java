package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    channelRepository.save(channel);
    log.info("공용 채널 생성 성공- 채널 이름: {},채널 설명: {}", name, description);
    return channelMapper.toDto(channel);

  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    List<User> participants = userRepository.findAllById(request.participantIds());
    if (participants.size() != request.participantIds().size()) {
      List<UUID> ids = participants.stream().map(User::getId).toList();
      List<UUID> missingIds = request.participantIds().stream().filter(id -> !ids.contains(id))
          .toList();
      log.warn("채널 생성 실패 - 존재하지 않은 참가자 포함. 사라진 Id:{} , 참가자 인원수:{}", missingIds,
          participants.size());
      throw new DiscodeitException(ErrorCode.PARTICIPANTS_NOT_FOUND);
    }
    participants.forEach(user -> {
      ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
      channel.getReadStatuses().add(readStatus);
    });

    channelRepository.save(channel);
    log.info("프라이빗 채널 생성 완료 - 채널 Id:{},채널 참가 인원 수:{}", channel.getId(), participants.size());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
    return channelMapper.toDto(channel);

  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> mySubscribedChannels = channelRepository.findAllAccessibleByUserId(userId);
    if (mySubscribedChannels.isEmpty()) {
      return List.of();
    }

    List<UUID> channelIds = mySubscribedChannels.stream().map(Channel::getId).toList();
    readStatusRepository.findAllByChannelIdIn(channelIds);

    return mySubscribedChannels.stream()
        .map(channel -> channelMapper.toDto(channel))
        .toList();

  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("채널 조회 실패 - 존재하지 않는 ID: {}", channelId);
              return new ChannelNotFoundException(channelId);
            });
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("채널 업데이트 실패(프라이빗 채널 수정 시도)");
      throw new PrivateChannelUpdateException(channelId);
    }
    channel.update(newName, newDescription);
    log.info("채널 업데이트 성공- 새 채널 이름:{}, 새 채널 설명:{}", newName, newDescription);
    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("채널 삭제 실패 - 존재하지 않는 채널 Id:{}", channelId);
              return new ChannelNotFoundException(channelId);
            });

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());
    channelRepository.deleteById(channelId);
    log.info("채널 삭제 성공-삭제된 채널 Id:{}", channelId);
  }


}
