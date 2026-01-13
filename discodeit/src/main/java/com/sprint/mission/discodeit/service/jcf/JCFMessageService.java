package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        this.data = new HashMap<>();
    }

    @Override
    public Message create(UUID channelId, UUID senderId, String content) {

        if (channelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (userService.findById(senderId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }


        Message message = new Message(channelId, senderId, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("Message with id " + id + " does not exist");
        }

        return data.get(id);
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findBySenderId(UUID senderId) {
        return data.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("Message with id " + id + " does not exist");
        }
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        message.updateContent(content);

        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("Message with id " + id + " does not exist");
        }

        data.remove(id);
    }
}
