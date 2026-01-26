//인터페이스에 뭘 구성해야 할까 따지고 보면 유저랑 똑같다.
//자 일단 채널생성, 단건다건조회, 채널수정, 채널삭제 쉽지?

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    //생성
    Channel create(String name, ChannelType type);
    //단건 조회
    Channel find(UUID channelId);
    //다건조회
    List<Channel> findAll();
    //채널수정
    Channel update(UUID channelId, String name, ChannelType type);
    //채널삭제
    void delete(UUID channelId);
}