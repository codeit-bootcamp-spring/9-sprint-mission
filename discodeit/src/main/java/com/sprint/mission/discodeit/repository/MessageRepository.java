package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    void save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    List<Message> findByChannelId(UUID channelId); // 채널별 메시지 조회용
    Optional<Message> findLatestByChannelId(UUID channelId);
    void delete(UUID id);
}