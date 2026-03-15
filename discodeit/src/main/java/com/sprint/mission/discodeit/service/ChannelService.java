package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto createPublicChannel(PublicChannelCreateRequest request, UUID creatorId);

  ChannelDto createPrivateChannel(PrivateChannelCreateRequest request, UUID creatorId);

  ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

  void delete(UUID channelId);

  void addParticipant(UUID channelId, UUID userId);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto findById(UUID id);
}