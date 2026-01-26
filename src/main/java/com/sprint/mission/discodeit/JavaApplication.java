package com.sprint.mission.discodeit;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    static void userCRUDTest(UserService userService) {
        System.out.println("------------------------------------------------------------");
        // 생성
        User user = userService.create("김용준", "1111@codeit.com", "010-1111-1111");
        System.out.println("유저 <" + user.getId() + "> 님이 생성 되었습니다.");
        System.out.println("생성시간 - " + user.getCreatedAt());
        System.out.println("------------------------------------------------------------");
        System.out.println("유저 이름: " + user.getUsername());
        System.out.println("유저 이메일: " + user.getEmail());
        System.out.println("유저 휴대전화: " + user.getPhone());
        System.out.println("------------------------------------------------------------");

        // 조회
        User foundUser = userService.find(user.getId());
        System.out.println("유저 조회(단건): " + foundUser.getId());
        System.out.println("------------------------------------------------------------");

        List<User> foundUsers = userService.findAll();
        System.out.println("유저 조회(다건): " + foundUsers.size());
        System.out.println("------------------------------------------------------------");

        // 수정
        User updatedUser = userService.updated(user.getId(), null, null, "woody5678");
        System.out.println("유저 수정: " + String.join("/", updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getPhone()));
        System.out.println("수정시간 - " + user.getUpdatedAt());
        System.out.println("------------------------------------------------------------");

        // 삭제
        userService.delete(user.getId());
        List<User> foundUsersAfterDelete = userService.findAll();
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
        System.out.println("------------------------------------------------------------\n\n");
    }


    static void channelCRUDTest(ChannelService channelService) {
        // 생성
        Channel channel = channelService.create("공지", ChannelType.TEXT);
        System.out.println("채널 생성: " + channel.getId());
        System.out.println("채널 이름: " + channel.getName());
        System.out.println("생성시간 - " + channel.getCreatedAt());
        System.out.println("------------------------------------------------------------");

        // 조회
        Channel foundChannel = channelService.find(channel.getId());
        System.out.println("채널 조회(단건): " + channel.getId());
        System.out.println("------------------------------------------------------------");

        List<Channel> foundChannels = channelService.findAll();
        System.out.println("채널 조회(다건): " + foundChannels.size());
        System.out.println("------------------------------------------------------------");

        // 수정
        Channel updatedChannel = channelService.update(channel.getId(), "공지사항", null);
        System.out.println("채널 수정: " + updatedChannel.getName() +
                " / 타입: " + updatedChannel.getType() +
                " / 생성일: " + updatedChannel.getCreatedAt() +
                " / 수정일: " + updatedChannel.getUpdatedAt());
        System.out.println("------------------------------------------------------------");

        //삭제
        channelService.delete(channel.getId());
        List<Channel> foundChannelsAfterDelete = channelService.findAll();
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
        System.out.println("------------------------------------------------------------\n\n");
    }


    static void messageCRUDTest(MessageService messageService) {
        // 생성
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Message message = messageService.create(userId, channelId, "안녕하세요.");
        System.out.println("메시지 생성: " + message.getId());
        System.out.println("메세지 생성 시간 -  " + message.getCreatedAt());
        System.out.println("메시지 내용: " + message.getContent());
        System.out.println("------------------------------------------------------------");

        // 조회
        Message foundMessage = messageService.find(message.getId());
        System.out.println("메시지 조회(단건): " + foundMessage.getId());
        System.out.println("------------------------------------------------------------");

        List<Message> foundMessages = messageService.findAll();
        System.out.println("메시지 조회(다건): " + foundMessages.size());
        System.out.println("------------------------------------------------------------");

        // 수정
        Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
        System.out.println("메세지 수정 내용: " + updatedMessage.getContent());
        System.out.println("수정 시간 -  " + updatedMessage.getUpdatedAt());
        System.out.println("------------------------------------------------------------");

        // 삭제
        messageService.delete(message.getId());
        List<Message> foundMessagesAfterDelete = messageService.findAll();
        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
    }


    public static void main(String[] args) {
        // 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        // 테스트
        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(messageService);
    }
}