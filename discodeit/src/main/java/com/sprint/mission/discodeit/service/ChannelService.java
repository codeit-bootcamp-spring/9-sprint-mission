package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {
    Channel createChannel(Channel channel);

    // 2. 단건 조회 (Read One)
    Channel getChannel(UUID id);

    // 3. 전체 조회 (Read All)
    List<Channel> getAllChannels();

    // 4. 수정 (Update) - ID와 수정할 정보를 담은 객체를 받음
    Channel updateChannel(UUID id, Channel channel);

    // 5. 삭제 (Delete)
    void deleteChannel(UUID id);
}
