package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    Channel channel = new Channel(request.name(), request.description(), ChannelType.PUBLIC);
    User creator = userRepository.findById(creatorId).orElseThrow();
    channel.addParticipant(creator);

    Channel savedChannel = channelRepository.save(channel);

    // 공개 채널도 생성자는 즉시 읽음 상태가 있어야 함
    readStatusRepository.save(new ReadStatus(creator, savedChannel, Instant.now()));

    return channelMapper.toDto(savedChannel);
  }

  // BasicChannelService.java 수정
  @Override
  @Transactional
  public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request, UUID creatorId) {
    Channel channel = new Channel("비밀 대화방", "개인 메시지 함", ChannelType.PRIVATE);

    User creator = userRepository.findById(creatorId).orElseThrow();
    channel.addParticipant(creator); // 나 자신 추가

    if (request.participantIds() != null) {
      userRepository.findAllById(request.participantIds()).forEach(channel::addParticipant);
    }

    // saveAndFlush를 통해 DB와 동기화 (C++의 fflush() 느낌)
    Channel savedChannel = channelRepository.saveAndFlush(channel);

    // 모든 참여자의 읽음 상태 생성
    savedChannel.getParticipants().forEach(user ->
        readStatusRepository.save(new ReadStatus(user, savedChannel, Instant.now()))
    );

    // [중요] 여기서 Flush를 한 번 더 해서 Repository 쿼리 시점에 데이터가 보이게 함
    readStatusRepository.flush();

    return channelMapper.toDto(savedChannel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // [수정] 공개 채널과 참여 채널을 모두 가져오도록 변경 (Issue 2 해결)
    List<Channel> channels = channelRepository.findAllByUserIdOrPublic(userId);
    return channelMapper.toDtoList(channels);
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId).orElseThrow();
    channel.update(request.newName(), request.newDescription());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    channelRepository.deleteById(channelId);
  }

  @Override
  @Transactional
  public void addParticipant(UUID channelId, UUID userId) {
    Channel channel = channelRepository.findById(channelId).orElseThrow();
    User user = userRepository.findById(userId).orElseThrow();
    channel.addParticipant(user);
  }

  @Override
  public ChannelDto findById(UUID id) {
    return channelRepository.findById(id).map(channelMapper::toDto).orElseThrow();
  }
}