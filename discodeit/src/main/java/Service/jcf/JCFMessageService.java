package Service.jcf;

import Service.ChannelService;
import Service.MessageService;
import Service.UserService;
import entity.Message;
import exception.NotFoundException;

import java.util.*;

public class JCFMessageService implements MessageService {
    //저장소(단건 조회)
    private final Map<UUID, Message> data = new HashMap<>();

    //의존성 주입
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

//    생성
//    의존성 주입 -> 채널과 유저가 없으면 메세지도 출력없게 만들어봄 근데 작동해서 일단 놔뒀음..
    @Override
    public Message create(UUID channaId, UUID senderId, String content) {
        if (!channelService.exitsById(channaId)) {
            throw new NotFoundException("Channel not found. channelId = " + channaId);
        }
        if (!userService.exitsById(senderId)) {
            throw new NotFoundException("User not found. senderId = " + senderId);
        }
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content must not be blank.");
        }
        Message message = new Message(channaId, senderId, content);
        data.put(message.getId(), message);
        return message;
    }

    //수정
    @Override
    public Message update(UUID messageId, String content) {
        Message message = data.get(messageId);
        if (message == null) {
            return null;
        }
        message.update(content);
        return message;
    }

    //단건
    @Override
    public Message findById(UUID messageId) {
        return data.get(messageId);
    }

    //전체
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    //삭제
    @Override
    public void delete(UUID messageId) {
        Message message = data.remove(messageId);
        if (message == null) {
            throw new NotFoundException("Message not found. id= " + messageId);
        }
    }

    //확인
    @Override
    public boolean existById(UUID messageId) {
        return data.containsKey(messageId);
    }
}


