package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {

    // 생성
    Message create(UUID channelId, UUID senderId, String content);
    // 단건 조회
    Message findById(UUID Id);
    // 채널 메세지 전체 조회
    List<Message> findByChannelId(UUID channelId);
    // 내가 보낸 메세지 조회
    List<Message> findBySenderId(UUID senderId);
    // 메세지 수정
    Message update(UUID id, String content);
    // 메세지 삭제
    void delete(UUID Id);


}
