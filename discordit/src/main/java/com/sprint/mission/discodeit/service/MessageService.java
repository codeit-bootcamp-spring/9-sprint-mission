package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;    //필요한 함수를 사용하기위해 외부의 라이브러리를 가져온다


public interface MessageService {

    Message create(String content, UUID channelId, UUID authorId);

    Message find(UUID id);

    List<Message> findAll();

    Message update(UUID id, String content);

    boolean delete(UUID id);
}