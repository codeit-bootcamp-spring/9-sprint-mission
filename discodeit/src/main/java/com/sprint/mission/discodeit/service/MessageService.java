package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    //생성
    Message createMessage(Message message);
    //단건 조회
    Message getMessage(UUID id);
    //전체 조회
    List<Message> getAllMessages();
    //수정
    Message updateMessage(UUID id, Message message);
    //삭제
    void deleteMessage(UUID id);
}
