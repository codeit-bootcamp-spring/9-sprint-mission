package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;

  @Override
  public Channel createPublicChannel(PublicChannelCreateRequest request, UUID creatorId) {
    Channel channel = new Channel(request.getName(), ChannelType.PUBLIC, request.getDescription());
    channel.getParticipantIds().add(creatorId);
    return channelRepository.save(channel);
  }

  @Override
  public Channel createPrivateChannel(PrivateChannelCreateRequest request, UUID creatorId) {
    Channel channel = new Channel(request.getName(), ChannelType.PRIVATE, request.getDescription());

    channel.getParticipantIds().add(creatorId);
    if (request.getParticipantIds() != null) {
      channel.getParticipantIds().addAll(request.getParticipantIds());
    }

    return channelRepository.save(channel);
  }

  @Override
  public List<Channel> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType() == ChannelType.PUBLIC ||
                channel.getParticipantIds().contains(userId)
        )
        .toList();
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널 없음"));
    channel.update(request.getName(), request.getDescription());
    return channelRepository.save(channel);
  }

  @Override
  public boolean delete(UUID channelId) {
    if (channelRepository.existsById(channelId)) {
      channelRepository.deleteById(channelId);
      return true;
    }
    return false;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    return channelRepository.findById(id);
  }

  @Override
  public void addParticipant(UUID channelId, UUID userId) {
    channelRepository.findById(channelId).ifPresent(channel -> {
      if (!channel.getParticipantIds().contains(userId)) {
        channel.getParticipantIds().add(userId);
        channelRepository.save(channel);
      }
    });
  }
}