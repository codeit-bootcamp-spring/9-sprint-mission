package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final List<Message> data = new ArrayList<>();
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("존재하지 않는 유저(authorId)입니다.");
        }
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("존재하지 않는 채널(channelId)입니다.");
        }
        Message message = new Message(content, channelId, authorId);
        data.add(message);
        return message;
    }

    @Override
    public Message find(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> findAll() {return new ArrayList<>(data);}

    @Override
    public Message update(UUID id, String content) {
        Message message = find(id);
        if (message != null) {
            message.setContent(content);
        }
        return message;
    }
    @Override
    public boolean delete(UUID id) {
        return data.removeIf(m -> m.getId().equals(id));
    }
}



