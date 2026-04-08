package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ForbiddenException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public ChannelDto createPublicChannel(PublicChannelCreateRequest request, UUID creatorId) {
    log.info("Creating public channel: '{}' by creator: {}", request.name(), creatorId);
    User creator = userRepository.findById(creatorId)
        .orElseThrow(() -> new UserNotFoundException(creatorId.toString()));

    Channel channel = new Channel(request.name(), request.description(), ChannelType.PUBLIC,
        creator);
    channel.addParticipant(creator);

    Channel saved = channelRepository.save(channel);
    readStatusRepository.save(new ReadStatus(creator, saved, Instant.now()));

    return channelMapper.toDto(saved);
  }

  @Override
  @Transactional
  public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request, UUID creatorId) {
    log.info("Creating private channel by creator: {}", creatorId);
    User creator = userRepository.findById(creatorId)
        .orElseThrow(() -> new UserNotFoundException(creatorId.toString()));

    Channel channel = new Channel("비밀 대화방", "개인 메시지 함", ChannelType.PRIVATE, creator);
    channel.addParticipant(creator);

    if (request.participantIds() != null && !request.participantIds().isEmpty()) {
      List<User> participants = userRepository.findAllById(request.participantIds());

      if (participants.size() != request.participantIds().size()) {
        throw new UserNotFoundException("일부 참여자를 찾을 수 없습니다.");
      }

      participants.forEach(channel::addParticipant);
    }

    Channel saved = channelRepository.saveAndFlush(channel);

    saved.getParticipants().forEach(user ->
        readStatusRepository.save(new ReadStatus(user, saved, Instant.now()))
    );
    readStatusRepository.flush();

    return channelMapper.toDto(saved);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.debug("Finding all channels for user: {}", userId);

    List<Channel> channels = channelRepository.findAllByUserIdOrPublic(userId);

    return channelMapper.toDtoList(channels);
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request, UUID requesterId) {
    log.info("Updating channel: {} by user: {}", channelId, requesterId);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId.toString()));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateException();
    }

    if (!channel.getOwner().getId().equals(requesterId)) {
      throw new ForbiddenException("채널 수정 권한이 없습니다.");
    }

    channel.update(request.newName(), request.newDescription());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void delete(UUID channelId, UUID requesterId) {
    log.info("Deleting channel: {} by user: {}", channelId, requesterId);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId.toString()));

    if (!channel.getOwner().getId().equals(requesterId)) {
      throw new ForbiddenException("채널 삭제 권한이 없습니다.");
    }

    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.delete(channel);
  }

  @Override
  public ChannelDto findById(UUID id) {
    log.debug("Finding channel by id: {}", id);
    return channelRepository.findById(id)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new ChannelNotFoundException(id.toString()));
  }
}