package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    //메세지 생성
    Message create(UUID userId, UUID channelId, String content);
    //메세지 단건조회
    Message find(UUID messageId);
    //메세지 다건조회
    List<Message> findAll();
    //메세지 수정
    Message update(UUID messageId, String content);
    //메세지 삭제
    void delete(UUID messageId);
}