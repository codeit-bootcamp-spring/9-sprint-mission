package repository.jcf;

import entity.Message;
import repository.ChannelRepository;
import repository.MessageRepository;
import repository.UserRepository;

import java.util.*;

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
