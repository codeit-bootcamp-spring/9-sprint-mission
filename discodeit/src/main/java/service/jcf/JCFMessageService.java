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
        if(messages.size()>0){
            return new ArrayList<>(messages);
        }else{
            System.out.println("메시지가 없습니다.");
            return null;
        }

    }
    @Override
    public List<Message> getSenderMessages(User sender) {
        List<Message> result =new ArrayList<>();
        messages.stream().filter(m->m.getSender().equals(sender)).forEach(result::add);
        return result;
    }
    @Override
    public List<Message> getReceiverMessages(User receiver) {
        List<Message> result =new ArrayList<>();
        if(messages==null||messages.size()==0) System.out.println("받은 메시지가 없습니다.");
        messages.stream().filter(message->message.getReceiver().equals(receiver)).forEach(result::add);
        return result;

    }
    @Override
    public boolean deleteMessage(String message,User loginUser){
      return  messages.removeIf(m->m.getContent().equals(message)&&m.getSender().equals(loginUser) || m.getReceiver().equals(loginUser));

    }
}
