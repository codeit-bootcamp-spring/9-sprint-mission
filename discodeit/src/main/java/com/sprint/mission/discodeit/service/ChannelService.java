package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public interface ChannelService {
    Channel create(PublicChannelCreateRequest request);

    Channel create(PrivateChannelCreateRequest request);

    ChannelDto find(UUID channelId);

    // 기존 List 반환에서 PageResponse 반환으로 변경하고 page, size 파라미터를 추가합니다.
    PageResponse<ChannelDto> findAllByUserId(UUID userId, int page, int size);

    Channel update(UUID channelId, PublicChannelUpdateRequest request);
    void delete(UUID channelId);
}