package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateMessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    boolean create(CreateMessageDto requestDto);

    boolean update(UUID userId, UUID messageId, String content);

    List<String> findAllForSender(UUID userId);

    List<String> findAllInChannel(String name);

    boolean delete(UUID userId, UUID messageId);

    void deleteAll(UUID channelId);

    String lastMessageTime(String channelName);
}
