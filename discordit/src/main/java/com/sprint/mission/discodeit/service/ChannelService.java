package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Channel; // 아까 만든 Channel 클래스 가져오기
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(String displayName,ChannelType type);

    Channel find(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String displayName);

    boolean delete(UUID id);

}