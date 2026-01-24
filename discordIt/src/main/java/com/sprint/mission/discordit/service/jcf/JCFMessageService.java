package service.jcf;

import entity.Message;
import service.ChannelService;
import service.MessageService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//implements : MessageService에 있는 기능을 수행하겠다는 약속
public class JCFMessageService implements MessageService {
    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

        public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    // Override : 인터페이스에 적혀있는 기능을 그대로 가져와서 실제 동작을 채워 넣는다는 표시

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {

        // 1. 유저가 진짜 있는지 확인
        if (userService.find(authorId) == null) {
            System.out.println("오류: 존재하지 않는 유저(ID: " + authorId + ")입니다.");
            return null;
        }

        // 2. 채널이 진짜 있는지 확인
        if (channelService.find(channelId) == null) {
            System.out.println("오류: 존재하지 않는 채널(ID: " + channelId + ")입니다.");
            return null;
        }

        Message message = new Message(content, channelId, authorId);
        data.add(message);
        return message;
    }

    @Override
    public Message find(UUID id) {
            return data.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        }

    @Override
    public List<Message> findAll() {
            return new ArrayList<>(data);
        }

    @Override
    public Message update(UUID id, String content) { Message message = find(id);
            if (message != null) {
                message.update(content); return message; } return null;
        }

    @Override
    public void delete(UUID id) {
            Message message = find(id);
            if (message != null) data.remove(message);
        }
}