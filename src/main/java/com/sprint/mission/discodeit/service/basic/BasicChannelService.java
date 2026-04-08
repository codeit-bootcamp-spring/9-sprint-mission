package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채널 서비스 구현체.
 * PUBLIC/PRIVATE 채널 생성, 수정, 삭제를 담당한다.
 * PRIVATE 채널 생성 시 참여자별 ReadStatus를 자동 생성하며,
 * 채널 삭제 시 관련 메시지의 첨부파일도 함께 정리한다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.info("PUBLIC 채널 생성 요청: name={}", request.name());
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    channel = channelRepository.save(channel);
    Instant lastMessageAt = messageRepository.findLastCreatedAtByChannelId(channel.getId())
        .orElse(null);
    log.debug("PUBLIC 채널 생성 완료: channelId={}", channel.getId());
    return channelMapper.toDto(channel, List.of(), lastMessageAt);
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.info("PRIVATE 채널 생성 요청: participantIds={}", request.participantIds());
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channel = channelRepository.save(channel);

    Channel finalChannel = channel;
    List<User> participants = request.participantIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(
                () -> new UserNotFoundException(Map.of("userId", userId))))
        .toList();

    participants.stream()
        .map(user -> new ReadStatus(user, finalChannel, Instant.now()))
        .forEach(readStatusRepository::save);

    List<UserDto> participantDtos = participants.stream()
        .map(userMapper::toDto)
        .toList();

    log.debug("PRIVATE 채널 생성 완료: channelId={}", channel.getId());
    return channelMapper.toDto(channel, participantDtos, null);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findAllByUserWithDetails(ChannelType.PUBLIC, userId);
    List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

    Map<UUID, Instant> lastMessageAtMap = messageRepository
        .findLastCreatedAtByChannelIds(channelIds)
        .stream()
        .collect(Collectors.toMap(
            row -> (UUID) row[0],
            row -> (Instant) row[1]
        ));

    return channels.stream()
        .map(channel -> {
          Instant lastMessageAt = lastMessageAtMap.get(channel.getId());
          List<UserDto> participants = channel.getType() == ChannelType.PRIVATE
              ? channel.getReadStatuses().stream()
                  .map(rs -> userMapper.toDto(rs.getUser()))
                  .toList()
              : List.of();
          return channelMapper.toDto(channel, participants, lastMessageAt);
        })
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateException(Map.of("channelId", channelId));
    }

    log.info("채널 수정 요청: channelId={}", channelId);
    channel.update(request.newName(), request.newDescription());
    Instant lastMessageAt = messageRepository.findLastCreatedAtByChannelId(channelId).orElse(null);
    log.debug("채널 수정 완료: channelId={}", channelId);
    return channelMapper.toDto(channel, List.of(), lastMessageAt);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    List<BinaryContent> attachments = messageRepository.findAllByChannel_Id(channelId).stream()
        .flatMap(m -> m.getAttachments().stream())
        .toList();
    List<UUID> attachmentIds = attachments.stream().map(BinaryContent::getId).toList();

    channelRepository.delete(channel);

    attachmentIds.forEach(id -> {
      binaryContentStorage.delete(id);
      binaryContentRepository.deleteById(id);
    });
    log.info("채널 삭제 완료: channelId={}", channelId);
  }
}
