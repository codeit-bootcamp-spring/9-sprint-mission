package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // ===== 기존 CRUD (절대 건드리지 않음) =====
    void create(Channel channel);
    Channel findByName(String name);
    List<Channel> findAll();
    boolean update(UUID id, String name, String description, boolean isPrivate);
    boolean delete(UUID id);

    // ===== DTO 기반 신규 기능 =====
    ChannelResponse create(ChannelCreateRequest request);

    ChannelResponse find(UUID channelId);

    List<ChannelResponse> findAllDto();

    List<ChannelResponse> findAllByUserId(UUID userId);
}

