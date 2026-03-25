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

@Slf4j
@RequiredArgsConstructor
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
    log.info("공개 채널 생성 요청 수신 - Name: {}", request.name());
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    channelRepository.save(channel);
    log.info("공개 채널 생성 완료 - ID: {}, Name: {}", channel.getId(), name);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 요청 수신 - 참여자 수: {}명", request.participantIds().size());

    Channel channel = new Channel(ChannelType.PRIVATE, "", "Private Channel");
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
        .map(user -> {
          log.debug("채널 참여자 등록 - User: {}, ChannelID: {}", user.getUsername(), channel.getId());
          return new ReadStatus(user, channel, channel.getCreatedAt());
        })
        .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("비공개 채널 생성 및 참여자 등록 완료 - ID: {}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.info("채널 수정 요청 수신 - ID: {}, New Name: {}", channelId, request.newName());

    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널 수정 실패 - 존재하지 않는 ID: {}", channelId);
          return new NoSuchElementException("Channel with id " + channelId + " not found");
        });
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("채널 수정 거부 - 비공개 채널은 수정할 수 없습니다. ID: {}", channelId);
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(newName, newDescription);
    log.info("채널 수정 완료 - ID: {}", channelId);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.info("채널 삭제 요청 수신 - ID: {}", channelId);

    if (!channelRepository.existsById(channelId)) {
      log.warn("채널 삭제 실패 - 존재하지 않는 ID: {}", channelId);
      throw new NoSuchElementException("Channel with id " + channelId + " not found");
    }
    log.debug("채널 연관 데이터(메시지, 읽기상태) 삭제 시작 - ChannelID: {}", channelId);
    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);

    channelRepository.deleteById(channelId);
    log.info("채널 삭제 완료 - ID: {}", channelId);
  }
}
