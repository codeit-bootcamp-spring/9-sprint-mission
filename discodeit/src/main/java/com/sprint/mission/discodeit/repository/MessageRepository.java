package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save (Message message);

    Optional<Message> findByMessageId(UUID messageId);

    List<Message> findBySenderId(UUID userId);

    List<Message> findByChannelId(UUID channelId);

    List<Message> findAllMessage();

    List<Message> findAllByChannelId(UUID channelId);

    void deleteById(UUID messageId);

    boolean existsById(UUID messageId);







}
