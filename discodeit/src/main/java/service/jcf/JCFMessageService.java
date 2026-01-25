package service.jcf;

import entity.*;
import service.ChannelService;
import service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageMap;

    private final Map<UUID, List<UUID>> messagesByUser;

    public JCFMessageService(){
        messageMap = new HashMap<>();

        messagesByUser = new HashMap<>();
    }

    @Override
    public Message create(UUID channelId, UUID writerId, String content) {
        Message message = new Message(channelId, writerId, content);
        UUID id = message.getId();
        messageMap.put(id, message);

        return message;
    }

    @Override
    public void remove(UUID id){
        Message removedMessage = messageMap.remove(id);
        if (removedMessage == null){
            throw new IllegalStateException("메시지 삭제 실패 (해당 메시지가 존재하지 않음) | 메시지ID: " + id);
        }
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
    public Message updateContent(UUID id, String newContent) {
        Message message = messageMap.get(id);
        if (message == null){
            throw new IllegalStateException("메시지 수정 실패 (해당 메시지가 존재하지 않음) | 메시지ID: " + id);
        }
        message.updateContent(newContent);
        return message;
    }
}
