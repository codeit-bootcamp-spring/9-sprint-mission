package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePrivateChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePublicChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Response.FindChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {

    Channel createPrivateChannel(CreatePrivateChRequest request);

    Channel createPublicChannel(CreatePublicChRequest request);

    void remove(UUID id);

    FindChannelResponse findByID(UUID id);

    List<FindChannelResponse> findAll();

    List<FindChannelResponse> findAllByUserId(UUID userId);

    Channel update(UpdateChannelRequest request);


    boolean addMember(UUID channelID, UUID userId);

    boolean removeMember(UUID channelID, UUID userId);

    boolean addMessage(UUID channelID, UUID messageId);

    boolean removeMessage(UUID channelID, UUID messageId);
}
