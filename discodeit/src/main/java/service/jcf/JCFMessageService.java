package service.jcf;

import entity.*;
import service.ChannelService;
import service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageMap;

    private final ChannelService channelService;
    //private final UserService userService;

    public JCFMessageService(ChannelService channelService){
        this.channelService = channelService;
        //this.userService = userService;
        messageMap = new HashMap<>();
    }

    @Override
    public Message Create(UUID writerId, UUID channelId, String content) {
        Message message = new Message(writerId, channelId, content);
        UUID id = message.getId();
        messageMap.put(id, message);
        this.channelService.addMessage(channelId, message);
        return message;
    }

    @Override
    public void Remove(UUID id){
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
    public void modifyContent(UUID id, String newContent) {
        Message message = messageMap.get(id);
        message.updateContent(newContent);
    }
}
