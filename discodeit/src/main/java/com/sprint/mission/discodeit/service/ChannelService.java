package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelResponse create(PublicChannelCreateRequest request);

  ChannelResponse create(PrivateChannelCreateRequest request, UUID requesterId);

  ChannelResponse find(UUID channelId);

  List<ChannelResponse> findAllByUserId(UUID userId);

  ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request);

  void delete(UUID channelId);
}
