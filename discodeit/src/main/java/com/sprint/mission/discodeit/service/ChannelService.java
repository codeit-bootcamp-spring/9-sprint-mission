package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 1. 생성
    void create(Channel channel);
    // 2. 조회
    Channel findByName(String channelName);

    List<Channel> findAll();
    // 3. 수정
    boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate);

    // 4. 삭제
    boolean delete(UUID id);
}
