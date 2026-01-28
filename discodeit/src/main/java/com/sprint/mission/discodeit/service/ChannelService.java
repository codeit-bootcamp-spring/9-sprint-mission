package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Optional<ChannelDto.Response> createPublicChannel(ChannelDto.CreatePublicRequest request);
    Optional<ChannelDto.Response> createPrivateChannel(ChannelDto.CreatePrivateRequest request);
    Optional<ChannelDto.Response> findById(UUID id);
    List<ChannelDto.Response> findAll();
    List<ChannelDto.Response> findAllByUserId(UUID userId);
    Optional<ChannelDto.Response> update(UUID id, String name, String description);
    boolean delete(UUID id);
}