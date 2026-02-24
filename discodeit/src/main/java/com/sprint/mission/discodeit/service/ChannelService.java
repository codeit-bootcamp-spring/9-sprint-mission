package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

  Channel createPublicChannel(PublicChannelCreateRequest request, UUID creatorId);

  Channel createPrivateChannel(PrivateChannelCreateRequest request, UUID creatorId);

  List<Channel> findAllByUserId(UUID userId);

  Channel update(UUID channelId, PublicChannelUpdateRequest request);

  boolean delete(UUID channelId);

  Optional<Channel> findById(UUID id);

  void addParticipant(UUID channelId, UUID userId);
}