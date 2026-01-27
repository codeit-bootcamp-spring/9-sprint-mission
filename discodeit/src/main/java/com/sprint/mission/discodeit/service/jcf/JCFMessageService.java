package main.java.com.sprint.mission.discodeit.service.jcf;

import main.java.com.sprint.mission.discodeit.entity.Message;
import main.java.com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    public List<Message> data = new ArrayList<>(); //모든 메시지를 담는 data


    @Override
    public Message create(String msgText, UUID senderId, UUID channelId) {
        Message message = new Message(msgText, senderId,channelId);
        data.add(message);

        return message;
    }


    @Override
    public Message findId(UUID id) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                return  message;
            }
        }

        throw new IllegalArgumentException("존재하지 않는 유저입니다.");
    }

    @Override
    public List<Message> findAll()
    {
        return new ArrayList<>(data);
    }

    @Override
    public Message update(UUID id, String msgText) {
        Message message = findId(id);
        if(message == null){
            System.out.println("존재하지 않는 메시지입니다.");
            return null;
        }
        message.update(msgText);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = findId(id);
        data.remove(message);
    }
}
