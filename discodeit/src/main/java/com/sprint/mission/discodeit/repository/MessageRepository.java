package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAllByChannelId(UUID channelId);

    List<Message> findAll();

    boolean existsById(UUID id);
    void deleteById(UUID id);
    void deleteAllByChannel(UUID channelId);
    Optional<Message> findLatestByChannelId(UUID channelId);
}
