package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto create(PublicChannelCreateRequest request);      // Channel → ChannelDto
  ChannelDto create(PrivateChannelCreateRequest request);     // Channel → ChannelDto
  ChannelDto find(UUID channelId);
  List<ChannelDto> findAllByUserId(UUID userId);
  ChannelDto update(UUID channelId, PublicChannelUpdateRequest request); // Channel → ChannelDto
  void delete(UUID channelId);
}