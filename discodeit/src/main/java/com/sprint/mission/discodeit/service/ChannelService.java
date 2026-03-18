package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;


import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChannelService {

  ChannelDto create(PublicChannelCreateRequest request);

  ChannelDto create(PrivateChannelCreateRequest request);

  ChannelDto find(UUID channelId);

  Slice<ChannelDto> findAllByUserId(UUID userId, Pageable pageable);

  ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

  void delete(UUID channelId);
}