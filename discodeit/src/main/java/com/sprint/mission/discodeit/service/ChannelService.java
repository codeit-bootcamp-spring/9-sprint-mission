package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {

    // 생성
    Channel create(String name);
    // 단건 조회
    Channel findById(UUID Id);
    // 다건 조회
    List<Channel> findAll();
    // 수정
    Channel update(UUID id, String name);
    // 삭제
    void delete(UUID Id);

}
