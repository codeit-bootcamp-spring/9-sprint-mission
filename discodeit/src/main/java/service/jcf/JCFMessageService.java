package service.jcf;

import entity.Channel;
import entity.Message;
import entity.User;
import service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageMap = new HashMap<>();

    public JCFMessageService(){

    }

    @Override
    public Message Create(User writer, Channel channel, String content) {
        Message message = new Message(writer, channel, content);
        UUID id = message.getId();
        messageMap.put(id, message);
        return message;
    }

    @Override
    public void Remove(UUID id){
        messageMap.remove(id);
    }

    @Override
    public Message findByID(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public List<Message> getAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public void modifyContent(UUID id, String newContent) {
        Message message = messageMap.get(id);

        message.setContent(newContent);
    }
}
