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

        // 채널에 저장된 메시지 삭제
        UUID chId = removedMessage.getChannel();
        boolean ret = this.channelService.removeMessage(chId, removedMessage);
        // 채널이 존재하지만 메시지가 삭제되지 않는 경우 오류 처리
        // 채널이 존재하지 않아도 메시지 삭제는 되어야한다...(굳이 막을 필요가 없고 막는다면 이 메시지는 영원히 삭제가 되지 않음)
        //
        if (ret == false && this.channelService.findByID(chId) != null){
            messageMap.put(id, removedMessage);
            throw new IllegalStateException("메시지 삭제 실패 (채널 서비스에서 해당 메시지 삭제 실패) | 메시지ID: " + id);
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
    public boolean modifyContent(UUID id, String newContent) {
        Message message = messageMap.get(id);
        if (message == null){
            return false;
        }
        message.updateContent(newContent);
        return true;
    }
}
