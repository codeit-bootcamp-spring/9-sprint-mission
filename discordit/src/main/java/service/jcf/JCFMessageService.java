package service.jcf;

import entity.Channel;
import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    // 🔥 핵심: 생성자 주입
    public JCFMessageService(UserService userService,
                             ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }


    @Override
    public Message addMessage(String newMessage, UUID channelId, UUID authorId) {
        User user = userService.getUser(authorId);
        Channel channel = channelService.getChannel(channelId);

        Message message = new Message(newMessage, channelId, authorId);
        data.put(message.getId(), message);
        return message;

    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> getAllMessage() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateMessage(UUID id, String newMessage) {
        Message message = data.get(id);
        if(message!=null){
            message.updateMessage(id, newMessage);
        }
    }

    @Override
    public void deleteMessage(UUID id) {
        Message message = data.remove(id);
        System.out.println("======= (DELETE)삭제된 메세지 ======= \n" + message);
    }
}
/*
 private final Map<UUID, Message> data = new HashMap<>();

    private final UserService userService;
    private final ChannelService channelService;

    // 🔥 핵심: 생성자 주입
    public JCFMessageService(UserService userService,
                             ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {

        // 1️⃣ 유저 존재 검증
        User user = userService.getUser(userId);

        // 2️⃣ 채널 존재 검증
        Channel channel = channelService.getChannel(channelId);

        // 3️⃣ 메시지 생성
        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);

        // 4️⃣ 결과 출력 (요구사항 핵심)
        System.out.println(
                user.getDisplayName()
                        + " 님이 ["
                        + channel.getChannelName()
                        + "] 채널에 메시지를 작성했습니다: "
                        + content
        );

        return message;
 */
