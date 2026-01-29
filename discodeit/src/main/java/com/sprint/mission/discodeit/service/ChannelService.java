package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ChannelService.CreatePrivateChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.CreatePublicChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.FindChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelService.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface ChannelService {

    Channel createPrivateChannel(CreatePrivateChRequest createPrivateChRequest);

    Channel createPublicChannel(CreatePublicChRequest createPublicChRequest);

    void remove(UUID id);

    FindChannelResponse findByID(UUID id);

    List<FindChannelResponse> findAllByUserId(UUID userId);

    Channel update(UpdateChannelRequest updateChannelRequest);

    boolean addMember(UUID channelID, User user);

    boolean removeMember(UUID channelID, User user);

    boolean addMessage(UUID channelID, Message message);

    boolean removeMessage(UUID channelID, Message message);
}
