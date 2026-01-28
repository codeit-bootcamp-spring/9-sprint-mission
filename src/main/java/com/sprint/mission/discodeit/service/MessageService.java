package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {

    Message create(UUID channelId, UUID senderId, String content);
    Message findById(UUID Id);
    List<Message> findByChannelId(UUID channelId);
    List<Message> findAll();
    List<Message> findBySenderId(UUID senderId);
    Message update(UUID id, String content);
    void delete(UUID id);


}
