package service.jcf;

import entity.Message;
import entity.User;
import service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private List<Message> messages;
    public JCFMessageService() {
        messages = new ArrayList<>();
    }

    @Override
    public void sendMessage(Message message){
        messages.add(message);
    }
    @Override
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }
    @Override
    public List<Message> getSenderMessages(User sender) {
        List<Message> result =new ArrayList<>();
        for(Message message : messages){
            if(message.getSender().equals(sender)){
                result.add(message);
            }
        }
        return result;
    }
    @Override
    public List<Message> getReceiverMessages(User receiver) {
        List<Message> result =new ArrayList<>();
        for(Message message:messages){
            if(message.getReceiver().equals(receiver)){
                result.add(message);
            }
        }
        return result;

    }
    @Override
    public boolean deleteMessage(String message){
        for(Message message1:messages){
            if(message1.getContent().equals(message)){
                messages.remove(message1);
                return true;
            }
        }
        return false;
    }
}
