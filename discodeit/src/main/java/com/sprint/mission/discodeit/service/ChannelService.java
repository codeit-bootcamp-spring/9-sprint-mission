package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;

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
