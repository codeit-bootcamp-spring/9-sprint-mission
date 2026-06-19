package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.exception.ErrorDetail;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final PageResponseMapper pageResponseMapper;
  private final SseService sseService;

  @CacheEvict(value = "channels", allEntries = true)
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.info("공개 채널 생성 로직 시작 - 채널명: {}", request.name());
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(name, description, ChannelType.PUBLIC);

    Channel saved = channelRepository.save(channel);
    ChannelDto dto = toDto(saved);
    sseService.broadcast("channels.created", dto);
    log.info("공개 채널 생성 및 DB 저장 완료!");
    return dto;
  }

  @CacheEvict(value = "channels", allEntries = true)
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 로직 시작");
    Channel channel = new Channel("비공개 채널", null, ChannelType.PRIVATE);
    Channel createdChannel = channelRepository.save(channel);

    request.participantIds().stream()
        .map(userId -> {
          User user = userRepository.findById(userId)
              .orElseThrow(() -> {
                log.warn("비공개 채널 참여자 연결 실패: 존재하지 않는 유저 ID 입니다. ({})", userId);
                return new UserException(ErrorCode.USER_NOT_FOUND,
                    List.of(new ErrorDetail("userId", userId.toString())));
              });
          return new ReadStatus(user, createdChannel, Instant.now(), true);
        })
        .forEach(readStatusRepository::save);

    ChannelDto dto = toDto(createdChannel);
    sseService.send(request.participantIds(), "channels.created", dto);
    log.info("비공개 채널 생성 및 참여자 연결 완료!");
    return dto;
  }

  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(this::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Cacheable(value = "channels", key = "#userId + '_' + #page")
  @Override
  public PageResponse<ChannelDto> findAll(UUID userId, int page) {
    Pageable pageable = PageRequest.of(page, 50);

    Slice<Channel> channelSlice = channelRepository.findAllByUserId(userId, pageable);

    Slice<ChannelDto> dtoSlice = channelSlice.map(channelMapper::toDto);

    return pageResponseMapper.fromSlice(dtoSlice);
  }

  @CacheEvict(value = "channels", allEntries = true)
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.info("채널 수정 로직 시작 - 대상 채널 ID: {}", channelId);
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널 수정 실패: 존재하지 않는 채널 ID 입니다. ({})", channelId);
          return new ChannelException(ErrorCode.CHANNEL_NOT_FOUND, channelId.toString());
        });
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("채널 수정 실패: 비공개 채널은 수정할 수 없습니다. 채널 ID: {}", channelId);
      throw new ChannelException(ErrorCode.PRIVATE_CHANNEL_UPDATE, channelId.toString());
    }
    channel.update(newName, newDescription);
    Channel saved = channelRepository.save(channel);
    ChannelDto dto = toDto(saved);
    sseService.broadcast("channels.updated", dto);
    log.info("채널 수정 완료 - 대상 채널 ID: {}", channelId);
    return dto;
  }

  @CacheEvict(value = "channels", allEntries = true)
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Override
  public void delete(UUID channelId) {
    log.info("채널 삭제 로직 시작 - 대상 채널 ID: {}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널 삭제 실패: 존재하지 않는 채널 ID 입니다. ({})", channelId);
          return new ChannelException(ErrorCode.CHANNEL_NOT_FOUND, channelId.toString());
        });

    ChannelDto deletedDto = toDto(channel);

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());
    channelRepository.deleteById(channelId);
    sseService.broadcast("channels.deleted", deletedDto);
    log.info("채널 삭제 완료 (연관된 메시지 및 읽음 상태 포함) - 대상 채널 ID: {}", channelId);
  }

  private ChannelDto toDto(Channel channel) {
    if (channel == null) {
      return null;
    }
    return channelMapper.toDto(channel);
  }
}