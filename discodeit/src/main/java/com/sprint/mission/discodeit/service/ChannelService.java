package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto createPublic(ChannelCreateRequestDto requestDto);
    ChannelResponseDto createPrivate(ChannelCreateRequestDto requestDto);
    ChannelResponseDto find(UUID channelId);
    List<ChannelResponseDto> findAllByUserId(UUID userId);
//    ChannelUpdateRequestDto에 Public만 수정 가능하도록 설정
    ChannelResponseDto update(UUID channelId, ChannelUpdateRequestDto requestDto);
    void delete(UUID channelId);
}
