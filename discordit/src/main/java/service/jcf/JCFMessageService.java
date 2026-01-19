package service.jcf;

import entity.User;
import entity.Channel;
import entity.Message;
import service.ChannelService;
import service.MessageService;
import service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;
    private UserService userService;
    private  ChannelService channelService;
    public JCFMessageService() {
        this.data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public boolean addMessage(Message message) {
        return data.add(message);
    }

    @Override
    public Message getChannelId(String ChannelId) {
        for (Message message : data) {
            if (message.getChannelId().equals(ChannelId)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public Message getContent(String Content) {
        for ( Message message : data) {
            if (message.getContent().equals(Content)) {
                return message;
            }
        }
        return null;
    }



    @Override
    public List<Message> getUsername(String username) {
        List<Message> result = new ArrayList<>();
        for ( Message message : data) {
            if (message.getUsername().equals(username)) {
               result.add(message);
            }
        }
        return result;
    }

    @Override
    public List<Message> getAllMessages() {
        return data;
    }

    @Override
    public Message updateMassage(String oldContent,String newContent, String userName, String channelId) {
        for (Message ms : data) {
            if (ms.getContent().equals(oldContent)) {
                ms.setContent(newContent);
                ms.setUserName(userName);
                ms.setChannelId(channelId);
                ms.setUpdatedAt(System.currentTimeMillis());
                return ms;
            }

        }
        return null;
    }

    @Override
    public boolean deleteMessage(String message) {
        return false;
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        // UUID를 내부에서 String으로 변환
        User user = userService.getbyId(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        Channel channel = channelService.getChannelById(channelId.toString());
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        Message message = new Message(
                userId.toString(),
                channelId.toString(),
                content
        );
        data.add(message);
        return message;
    }







}
