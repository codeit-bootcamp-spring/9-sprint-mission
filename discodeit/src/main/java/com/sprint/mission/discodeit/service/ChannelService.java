package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;

import java.util.*;

public interface ChannelService {

    ChannelDto createPrivateChannel(PrivateChannelCreateRequest request);

    ChannelDto createPublicChannel(PublicChannelCreateRequest request);

    void remove(UUID id);

    ChannelDto findByID(UUID id);

    List<ChannelDto> findAll();

    List<ChannelDto> findAllByUserId(UUID userId);

    List<UUID> findMessagesInChannel(UUID id);

    ChannelDto update(UUID id, ChannelUpdateRequest request);

    boolean addMember(UUID channelID, UUID userId);

    boolean removeMember(UUID channelID, UUID userId);

    boolean addMessage(UUID channelID, UUID messageId);

    boolean removeMessage(UUID channelID, UUID messageId);
}
