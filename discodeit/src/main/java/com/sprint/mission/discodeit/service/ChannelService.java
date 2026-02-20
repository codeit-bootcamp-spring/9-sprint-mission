package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;

import java.util.*;

public interface ChannelService {

    ChannelResponse createPrivateChannel(CreatePrivateChannelRequest request);

    ChannelResponse createPublicChannel(CreatePublicChannelRequest request);

    void remove(UUID id);

    ChannelResponse findByID(UUID id);

    List<ChannelResponse> findAll();

    List<ChannelResponse> findAllByUserId(UUID userId);

    List<UUID> findMessagesInChannel(UUID id);

    ChannelResponse update(UUID id, UpdateChannelRequest request);

    boolean addMember(UUID channelID, UUID userId);

    boolean removeMember(UUID channelID, UUID userId);

    boolean addMessage(UUID channelID, UUID messageId);

    boolean removeMessage(UUID channelID, UUID messageId);
}
