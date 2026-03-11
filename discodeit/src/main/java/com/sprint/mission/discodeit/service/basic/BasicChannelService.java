package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    // 1. 부모 엔티티 생성 (이때 ID는 null이어야 함, @GeneratedValue가 처리하도록)
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    // 2. 부모를 저장하고 ID를 할당받음
    Channel savedChannel = channelRepository.saveAndFlush(channel);

    // 3. 로그로 ID가 정상 출력되는지 확인 (디버깅용)
    System.out.println("생성된 채널 ID: " + savedChannel.getId());

    // 4. 참여자 순회
    for (UUID userId : request.participantIds()) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

      // 💡 중요: ReadStatus 엔티티 생성 시 반드시 'savedChannel' 객체 전체를 넘깁니다.
      ReadStatus readStatus = new ReadStatus(user, savedChannel, Instant.now());

      // 5. 자식 저장
      readStatusRepository.save(readStatus);
    }

    // 6. 자식들까지 모두 DB에 반영
    readStatusRepository.flush();

    return channelMapper.toDto(savedChannel);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
  }

  @Override
  public List<ChannelDto> findAll() {
    return channelRepository.findAll().stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(rs -> rs.getChannel().getId())
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
        )
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channels cannot be updated");
    }

    channel.update(request.newName(), request.newDescription());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    channelRepository.deleteById(channelId);
  }
}