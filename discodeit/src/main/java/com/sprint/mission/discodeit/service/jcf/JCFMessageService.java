package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();
    // [최적화] List 대신 Map을 사용하여 삭제 성능을 O(1)로 개선 (C++의 std::unordered_map<UUID, map<UUID, Message*>> 구조)
    private final Map<UUID, Map<UUID, Message>> channelMessagesIndex = new ConcurrentHashMap<>();

    public JCFMessageService(UserService userService, ChannelService channelService) {}

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
                channelMsgs.remove(id); // O(1) 삭제
            }
            return true;
        }
        return false;
    }
}