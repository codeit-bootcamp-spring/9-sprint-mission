package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Message>> channelMessagesIndex = new ConcurrentHashMap<>();

    private JCFMessageService() {}

    private static class InstanceHolder {
        private static final JCFMessageService INSTANCE = new JCFMessageService();
    }

    public static JCFMessageService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public List<Message> findAllByContentKeyword(String keyword) {
        return messageMap.values().stream()
                .filter(message -> message.getContent().contains(keyword))
                .toList();
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        channelMessagesIndex
                .computeIfAbsent(message.getChannelId(), k -> new ConcurrentHashMap<>())
                .put(message.getId(), message);
        return message;
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
    public void update(Message message) {
        if (messageMap.containsKey(message.getId())) {
            messageMap.put(message.getId(), message);
            channelMessagesIndex.get(message.getChannelId()).put(message.getId(), message);
        }
    }

    @Override
    public boolean delete(UUID id) {
        Message removed = messageMap.remove(id);
        if (removed != null) {
            Map<UUID, Message> channelMsgs = channelMessagesIndex.get(removed.getChannelId());
            if (channelMsgs != null) {
                channelMsgs.remove(id);
            }
            return true;
        }
        return false;
    }
}