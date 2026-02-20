package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelView;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDeleteRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelView createPublic(PublicChannelCreateRequest request);
    ChannelView createPrivate(PrivateChannelCreateRequest request);
    ChannelView update(ChannelUpdateRequest request);
    ChannelView findById(UUID channelId);
    List<ChannelView> findAllByUserId(UUID userId);
    void delete(ChannelDeleteRequest request);
    boolean existsById(UUID channelId);
}
