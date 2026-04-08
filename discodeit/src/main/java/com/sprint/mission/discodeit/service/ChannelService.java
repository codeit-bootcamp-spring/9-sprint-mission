package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel create(PublicChannelCreateRequest request);

  Channel create(PrivateChannelCreateRequest request);

  ChannelDto find(UUID channelId);

  PageResponse<ChannelDto> findAll(UUID userId, int page);

  Channel update(UUID channelId, PublicChannelUpdateRequest request);

  void delete(UUID channelId);
}