package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    log.debug("공개 채널 생성 시도, 이름={}, 설명={}", name, description);

    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    channelRepository.save(channel);

    log.info("공개 채널 생성 완료, id={}, 이름={}", channel.getId(), name);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.debug("비공개 채널 생성 시도, 참가자 수={}", request.participantIds().size());

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("비공개 채널 생성 완료, id={}, 참가자 수={}", channel.getId(), readStatuses.size());
    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    log.debug("채널 조회 시도, id={}", channelId);

    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> {
          log.warn("채널을 찾을 수 없습니다, id={}", channelId);
          return new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다: " + channelId);
        });
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.debug("사용자 채널 조회 시도, userId={}", userId);

    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    List<ChannelDto> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();

    log.info("사용자 채널 조회 완료, userId={}, 조회된 채널 수={}", userId, result.size());
    return result;
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("채널 업데이트 시도, id={}, 새 이름={}, 새 설명={}", channelId, request.newName(), request.newDescription());

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("업데이트할 채널을 찾을 수 없습니다, id={}", channelId);
          return new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다: " + channelId);
        });

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("비공개 채널은 업데이트할 수 없습니다, id={}", channelId);
      throw new IllegalArgumentException("비공개 채널은 업데이트할 수 없습니다.");
    }

    channel.update(request.newName(), request.newDescription());
    log.info("채널 업데이트 완료, id={}, 새 이름={}", channelId, request.newName());
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시도, id={}", channelId);

    if (!channelRepository.existsById(channelId)) {
      log.error("삭제할 채널을 찾을 수 없습니다, id={}", channelId);
      throw new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다: " + channelId);
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);

    log.info("채널 삭제 완료, id={}", channelId);
  }
}