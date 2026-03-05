package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDto createPublic(PublicChannelCreateRequest request);

    ChannelDto createPrivate(PrivateChannelCreateRequest request);

    ChannelDto findById(UUID channelId);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto update(UUID channelId, ChannelUpdateRequest request);

    void delete(UUID channelId);
}