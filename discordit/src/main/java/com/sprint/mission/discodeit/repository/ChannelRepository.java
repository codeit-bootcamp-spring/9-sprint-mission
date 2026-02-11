package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    boolean save(Channel channel);
    boolean save(UpdateChannelDto requestDto);
    String readChannel(String name);

    ChannelType getChannelType(String name);

    UUID getChannelId(String name);

    List<ResponseChannelDto> readAllChannel(String userName);

    List<ResponseChannelDto> findAllPrivateChannel(String userName);

    List<ResponseChannelDto> accessiblePrivateChannel(String userName);

    ResponseChannelDto requestChannelInfo(Channel channel);

    void includePrivateChannel(String channelName, String userName, UUID userId);

    void excludePrivateChannel(String channelName, String userName);

    boolean deleteChannel(String name);

    void deleteAllChannel(String name);

    boolean isPresentChannel(String name);

    boolean isCreatePrivateChannel(String name);

    UUID channelNameToId(String name);

    String channelIdToName(UUID id);
}
