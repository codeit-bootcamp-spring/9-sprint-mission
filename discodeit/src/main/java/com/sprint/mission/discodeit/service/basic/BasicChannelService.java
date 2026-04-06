package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAtProjection;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.debug("Public 채널 생성 시작: {}", request);
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    ChannelDto dto = channelMapper.toDto(channelRepository.save(channel), List.of(), Instant.MIN);
    log.info("Public 채널 생성 완료. 채널ID: {}, 채널명: {}", dto.name(), dto.name());
    return dto;
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.debug("Private 채널 생성 시작: {}", request);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    List<User> participants = userRepository.findAllById(request.participantIds());
    if (participants.size() != request.participantIds().size()) {
      log.warn("사용자 추가 오류. 요청 사용자 ID와 실제 사용자의 수가 다름");
      log.warn("입력 사용자 수: {}, 실제 사용자 수: {}", request.participantIds().size(), participants.size());
      throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND,
          Map.of("요청한 사용자 수", request.participantIds().size(), "실제 사용자 수", participants.size()));
    }

    List<UserDto> participantsDto = participants.stream().map(userMapper::toDto).toList();

    List<ReadStatus> readStatusList = participants.stream()
        .map(user -> new ReadStatus(user, createdChannel, channel.getCreatedAt()))
        .toList();

    readStatusRepository.saveAll(readStatusList);
    log.debug("readStatusList 생성 완료. readStatusList 크기: {}", readStatusList.size());

    ChannelDto dto = channelMapper.toDto(createdChannel, participantsDto,
        Instant.MIN);
    log.info("Private 채널 생성 완료: {}", channel);
    return dto;
  }

  @Override
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("존재하지 않는 채널 조회 시도. 채널 ID: {}", channelId);
          return new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND,
              Map.of("channelId", channelId));
        });

    Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channelId)
        .orElse(channel.getCreatedAt());

    List<UserDto> participants = readStatusRepository.findAllByChannel_Id(channelId).stream()
        .map(readStatus -> userMapper.toDto(readStatus.getUser()))
        .toList();

    return channelMapper.toDto(channel, participants, lastMessageAt);
  }

  @Override
  public List<ChannelDto> findByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findAccessibleChannelsByUserId(userId);
    if (channels.isEmpty()) {
      return List.of();
    }

    List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

    Map<UUID, Instant> lastMessageMap = messageRepository.findLastMessageAtByChannelIds(channelIds)
        .stream()
        .collect(Collectors.toMap(
            MessageAtProjection::getChannelId,
            MessageAtProjection::getLastAt
        ));

    Map<UUID, List<UserDto>> participantsMap = readStatusRepository.findAllByChannelIdsWithUser(
            channelIds)
        .stream()
        .collect(Collectors.groupingBy(
            readStatus -> readStatus.getChannel().getId(),
            Collectors.mapping(rs -> userMapper.toDto(rs.getUser()), Collectors.toList())
        ));

    return channels.stream()
        .map(channel -> {
          UUID id = channel.getId();
          List<UserDto> participants = (channel.getType() == ChannelType.PRIVATE)
              ? participantsMap.getOrDefault(id, List.of())
              : List.of();

          return channelMapper.toDto(
              channel,
              participants,
              lastMessageMap.getOrDefault(id, channel.getCreatedAt())
          );
        })
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("채널 수정 진입. 채널ID: {}", channelId);
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널 조회 실패. 채널 ID: {}", channelId);
          return new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND,
              Map.of("channelId", channelId));
        });
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("Private 채널 수정 시도. 채널 ID: {}", channelId);
      throw new PrivateChannelUpdateException(ErrorCode.PRIVATE_CHANNEL_UPDATE,
          Map.of("channelId", channelId));
    }
    channel.update(newName, newDescription);
    Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channelId)
        .orElse(channel.getCreatedAt());

    ChannelDto dto = channelMapper.toDto(channelRepository.save(channel), List.of(), lastMessageAt);
    log.info("채널 수정 완료: {}", channel);
    return dto;
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    log.warn("채널 삭제 진입: {}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("존재하지 않는 채널 삭제 시도: {}", channelId);
          return new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND,
              Map.of("channelId", channelId));
        });

    messageRepository.deleteAllByChannelId(channel.getId());
    log.info("채널 내 메시지 삭제 완료");
    readStatusRepository.deleteAllByChannel_Id(channel.getId());
    log.info("채널 내 메시지 읽음 상태 삭제 완료");

    channelRepository.deleteById(channelId);
    log.info("채널 삭제 완료");
  }
}
