package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap;

    public JCFMessageRepository(){
        messageMap = new HashMap<>();
    }
    @Override
    public void save(Message message) {
        UUID id = message.getId();
        messageMap.put(id, message);
    }

    @Override
    public boolean remove(UUID id) {
        return messageMap.remove(id) != null;
    }

    @Override
    public Message findByID(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }
}
