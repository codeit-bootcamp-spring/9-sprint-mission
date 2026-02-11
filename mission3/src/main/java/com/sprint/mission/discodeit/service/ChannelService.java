package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(ChannelType type, ChannelDto.PublicDto publicDto);
    Channel createPrivateChannel(ChannelType type,ChannelDto.PrivateDto privateDto);
    ChannelDto.FindDto findChannel(UUID id);
    List<ChannelDto.FindDto> findAllByUserId(UUID userId);
    Channel update(ChannelType channelType,ChannelDto.UpdateDto updateDto);
    void delete(UUID channelId);
}
