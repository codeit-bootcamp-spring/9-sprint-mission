package service.jcf;

import entity.*;
import service.ChannelService;
import service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageMap;

    //private final UserService userService;

    private final Map<UUID, List<UUID>> messagesByUser;

    public JCFMessageService(){
        messageMap = new HashMap<>();

        messagesByUser = new HashMap<>();
    }

    @Override
    public Message create(UUID writerId, UUID channelId, String content) {
        Message message = new Message(writerId, channelId, content);
        UUID id = message.getId();
        messageMap.put(id, message);

//        boolean result = this.channelService.addMessage(channelId, message);
//
//        if (result){
//            messagesByUser.computeIfAbsent(writerId, k -> new ArrayList<>()).add(id);
//        }
//        else{
//            messageMap.remove(id);
//            throw new IllegalStateException("메시지 생성 실패 (채널에 해당 메시지 추가 실패) | 메시지ID: " + id);
//        }

        return message;
    }

    @Override
    public void remove(UUID id){
        Message removedMessage = messageMap.remove(id);
        if (removedMessage == null){
            throw new IllegalStateException("메시지 삭제 실패 (해당 메시지가 존재하지 않음) | 메시지ID: " + id);
        }

//        UUID chId = removedMessage.getChannel();
//        boolean ret = this.channelService.removeMessage(chId, removedMessage);
//        if (!ret && this.channelService.findByID(chId) != null){
//            messageMap.put(id, removedMessage);
//            throw new IllegalStateException("메시지 삭제 실패 (채널 서비스에서 해당 메시지 삭제 실패) | 메시지ID: " + id);
//        }
//
//        List<UUID> messageListByUser = messagesByUser.get(removedMessage.getWriter());
////        if (messageListByUser == null){
////            return;
////        }
//       ret = messageListByUser.remove(id);
////        if (!ret){
////            messageMap.put(id, removedMessage);
////        }
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

    @Override
    public List<Message> findByUserID(UUID userId){
        List<UUID> result = messagesByUser.get(userId);
        if (messagesByUser.get(userId) == null){
            return null;
        }
        return result.stream().map(messageMap::get).filter(Objects::nonNull).toList();
    }
}
