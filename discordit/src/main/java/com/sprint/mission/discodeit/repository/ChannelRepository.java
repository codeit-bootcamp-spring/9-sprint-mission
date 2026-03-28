package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.RequestChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

  ResponseChannelDto save(RequestChannelDto requestDto);

  ResponseChannelDto save(UUID channelId, RequestUpdateChannelDto requestDto);

  ResponseChannelDto findChannel(UUID channelId, UUID userId);

  ChannelType getChannelType(UUID id);

  UUID getChannelId(String name);

  List<ResponseChannelDto> findAllChannel(UUID userId);

  List<ResponseChannelDto> findAllPrivateChannel(UUID userId);

  List<ResponseChannelDto> accessiblePrivateChannel(UUID userId);

  ResponseChannelDto toDto(Channel channel);

  void includePrivateChannel(String channelName, String username, UUID userId);

  void excludePrivateChannel(String channelName, String username);

  boolean deleteChannel(UUID id);

  boolean isPresentChannel(UUID id);

  UUID channelNameToId(String name);

  String channelIdToName(UUID id);

  ResponseChannelDto findChannel(UUID channelId);
}
