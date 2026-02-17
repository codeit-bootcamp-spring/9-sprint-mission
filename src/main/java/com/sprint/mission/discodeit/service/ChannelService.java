package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublic(CreatePublicChannelRequest request);
    ChannelResponse createPrivate(CreatePrivateChannelRequest request);
    ChannelResponse findById(UUID channelId);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(UUID channelId, UpdateChannelRequest request);
    void delete(UUID channelId);
}
