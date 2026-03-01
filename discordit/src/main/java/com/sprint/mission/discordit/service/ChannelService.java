package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.data.ChannelDto;
import com.sprint.mission.discordit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discordit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discordit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto create(PublicChannelCreateRequest request);

  ChannelDto create(PrivateChannelCreateRequest request);

  ChannelDto find(UUID channelId);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

  void delete(UUID channelId);
}