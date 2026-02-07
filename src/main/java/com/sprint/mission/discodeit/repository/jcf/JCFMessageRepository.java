package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

    private final List<Message> messages = new ArrayList<>();

    @Override
    public Message save(Message message) {
        messages.add(message);
        return message;
    }

    @Override
    public Message find(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        messages.removeIf(message -> message.getId().equals(id));
    }
}
