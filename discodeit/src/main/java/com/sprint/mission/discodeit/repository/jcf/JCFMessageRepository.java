package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data = new HashMap<>();
    private final Map<UUID, List<UUID>> channelIndex = new HashMap<>();

    @Override
    public Message save(Message message) {
        Message existing = data.get(message.getId());

        if (existing == null) {
            data.put(message.getId(), message);
            channelIndex
                    .computeIfAbsent(message.getChannelId(), k -> new ArrayList<>())
                    .add(message.getId());
            return message;
        }

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if (messageId == null) return Optional.empty();
        return Optional.ofNullable(data.get(messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        if (channelId == null) return List.of();

        List<UUID> ids = channelIndex.get(channelId);
        if (ids == null || ids.isEmpty()) return List.of();

        List<Message> result = new ArrayList<>(ids.size());
        for (UUID id : ids) {
            Message m = data.get(id);
            if (m != null) result.add(m);
        }
        return result;
    }

    @Override
    public void delete(UUID messageId) {
        if (messageId == null) return;

        Message removed = data.remove(messageId);
        if (removed == null) return;

        List<UUID> ids = channelIndex.get(removed.getChannelId());
        if (ids != null) {
            ids.remove(messageId);
            if (ids.isEmpty()) channelIndex.remove(removed.getChannelId());
        }
    }

    @Override
    public boolean existsById(UUID messageId) {
        if (messageId == null) return false;
        return data.containsKey(messageId);
    }
}