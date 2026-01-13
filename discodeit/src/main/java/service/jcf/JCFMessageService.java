package service.jcf;

import entity.Message;
import service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data = new ArrayList<>();

    @Override
    public void delete(UUID id) {
        for (Message message : data) {
            if(message.getId().equals(id))
                data.remove(message);
        }
        System.out.println("없음");
    }


    @Override
    public List<Message> findAll() {
        return data;
    }

    @Override
    public Message find(UUID id) {
        for(Message message : data) {
            if(message.getId().equals(id))
            return message;
        }
        System.out.println("없음");
        return null;
    }

    @Override
    public Message update(String chat) {
       for(Message message : data) {
           if(message.getChat().equals(chat))
           message.setChat(chat);
       }
       System.out.println("없음");
       return null;
    }

    @Override
    public Message create(String chat) {
        Message newMessage = new Message(chat);
        data.add(newMessage);
        return newMessage;
    }
}
