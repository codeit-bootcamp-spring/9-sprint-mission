package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Optional<ChannelResponse> createChannel(ChannelCreateRequest request);
    Optional<ChannelResponse> findById(UUID id);
    List<ChannelResponse> findAllByUserId(UUID userId);
    Optional<ChannelResponse> update(UUID id, String name, String description);
    boolean delete(UUID id);
}