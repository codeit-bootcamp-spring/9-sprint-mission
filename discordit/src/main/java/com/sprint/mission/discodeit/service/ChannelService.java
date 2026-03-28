package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseFindChannelDto;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  boolean isPresent(UUID id);

  ResponseChannelDto create(RequestChannelDto requestDto);

  ResponseFindChannelDto find(UUID channelId);

  List<ResponseChannelDto> findAllPrivateChannel(UUID userId);

  List<ResponseChannelDto> findAll(UUID userId);

  ResponseChannelDto update(UUID channelId, RequestUpdateChannelDto requestDto);

  boolean delete(UUID id);

  boolean includePrivateChannel(String channelName, String username, UUID userId);

  void excludePrivateChannel(String channelName, String username);
}
