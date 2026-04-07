package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Message>> channelMessagesIndex = new ConcurrentHashMap<>();

    private JCFMessageRepository() {}
    private static class Holder {
        private static final JCFMessageRepository INSTANCE = new JCFMessageRepository();
    }
    public static JCFMessageRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void save(Message message) {
        messageMap.put(message.getId(), message);
        channelMessagesIndex
                .computeIfAbsent(message.getChannelId(), k -> new ConcurrentHashMap<>())
                .put(message.getId(), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        Map<UUID, Message> channelMsgs = channelMessagesIndex.get(channelId);
        return (channelMsgs == null) ? Collections.emptyList() : new ArrayList<>(channelMsgs.values());
    }

    @Override
    public void delete(UUID id) {
        Message removed = messageMap.remove(id);
        if (removed != null) {
            Map<UUID, Message> channelMsgs = channelMessagesIndex.get(removed.getChannelId());
            if (channelMsgs != null) {
                channelMsgs.remove(id);
            }
        }
    }
}