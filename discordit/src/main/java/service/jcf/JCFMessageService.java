package service.jcf;

import entity.Message;
import service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data;
    public JCFMessageService(){
        this.data = new HashMap<>();
    }

    @Override
    public Message addMessage(String newMessage, UUID channelId, UUID authorId) {
        Message message = new Message(newMessage, channelId, authorId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> getAllMessage() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateMessage(UUID id, String newMessage) {
        Message message = data.get(id);
        if(message!=null){
            message.updateMessage(id, newMessage);
        }
    }

    @Override
    public void deleteMessage(UUID id) {
        Message message = data.remove(id);
        System.out.println("======= (DELETE)삭제된 메세지 ======= \n" + message);
    }
}
