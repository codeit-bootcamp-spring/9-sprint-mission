package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {
    static User setupUser(UserService userService) {
        UserCreateRequest request = new UserCreateRequest("woody", "woody@codeit.com", "woody1234");
        User user = userService.create(request, Optional.empty());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
        UserService userService = context.getBean(UserService.class);
        MessageService messageService = context.getBean(MessageService.class);
        ChannelService channelService = context.getBean(ChannelService.class);

        User user = setupUser(userService);

        System.out.println("첫번째 유저" + user.toString());
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, user);
    }
}
