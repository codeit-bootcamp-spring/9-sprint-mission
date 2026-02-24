package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(ChannelCreateRequest request);
    Channel createPrivate(ChannelCreateRequest request);
    Channel find(UUID id);
    List<ChannelDto> findAllByUserId(UUID userId);
    Channel update(UUID id, ChannelUpdateRequest request);
    void delete(UUID id);
}
