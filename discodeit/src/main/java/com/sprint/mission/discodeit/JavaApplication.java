package com.sprint.mission.discodeit;

import main.java.com.sprint.mission.discodeit.entity.Channel;
import main.java.com.sprint.mission.discodeit.entity.ChannelType;
import main.java.com.sprint.mission.discodeit.entity.Message;
import main.java.com.sprint.mission.discodeit.entity.User;
import main.java.com.sprint.mission.discodeit.repository.UserRepository;
import main.java.com.sprint.mission.discodeit.repository.file.FileUserRepository;
import main.java.com.sprint.mission.discodeit.service.ChannelService;
import main.java.com.sprint.mission.discodeit.service.MessageService;
import main.java.com.sprint.mission.discodeit.service.UserService;
import main.java.com.sprint.mission.discodeit.service.file.FileUserService;

public class JavaApplication {

    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", author.getId(), channel.getId());
        System.out.println("메시지 생성 완료! ID: " + message.getId());
        System.out.println("내용: " + message.getMsgText());
    }

    public static void main(String[] args) {
        UserRepository userRepository = new FileUserRepository();

        UserService userService = new FileUserService();

        ChannelService channelService = new main.java.com.sprint.mission.discodeit.service.jcf.JCFChannelService();
        MessageService messageService = new main.java.com.sprint.mission.discodeit.service.jcf.JCFMessageService();

        System.out.println("=== 테스트 환경 세팅 완료 ===");

        User user = setupUser(userService);
        System.out.println("유저 셋업 완료: " + user.getDisplayName());

        Channel channel = setupChannel(channelService);
        System.out.println("채널 셋업 완료: " + channel.getChannelName());

        System.out.println("--- 메시지 생성 테스트 시작 ---");
        messageCreateTest(messageService, channel, user);

        System.out.println("=== 모든 테스트 종료 ===");
    }
}