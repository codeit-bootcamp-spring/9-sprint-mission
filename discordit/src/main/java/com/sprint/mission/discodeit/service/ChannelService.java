package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.FindChannelDto;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    boolean isPresent(String name);
    boolean create(String type, String name, String createUserName);
    FindChannelDto find(String name);

    List<ResponseChannelDto> findAllPrivateChannel(String userName);

    List<ResponseChannelDto> findAll(String userName);
    boolean update(UpdateChannelDto requestDto);
    boolean delete(String name);

    void deleteAll(String name);

    boolean includePrivateChannel(String channelName, String userName, UUID userId);

    void excludePrivateChannel(String channelName, String userName);

    boolean isCeatePrivateChannel(String userName);
}
