package service.jcf;

import entity.User;
import entity.Channel;
import entity.Message;
import service.UserService;
import service.ChannelService;
import service.MessageService;

import java.util.UUID;

public class JCFDiscordService {
    private final JCFUserService userService;
    private final JCFChannelService channelService;
    private final JCFMessageService messageService;

    //생성자 초기화
    public JCFDiscordService(
            JCFUserService userService,
            JCFChannelService channelService,
            JCFMessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public Message sendMessage(String newMessage, UUID authorId, UUID channelId) {

        // 1. 유저 존재 확인
        userService.getUser(authorId);

        // 2. 채널 존재 확인
        channelService.getChannel(channelId);

        // 3. 메시지 생성
        return messageService.addMessage(newMessage, channelId, authorId);


    }


}
