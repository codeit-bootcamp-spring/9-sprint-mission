package com.example.demo;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {
        "com.example.demo",
        "com.sprint.mission.discodeit"
})
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        UserResponse user = setupUser(userService);
        var channel = setupChannel(channelService);
        messageCreateTest(messageService, channel.getId(), user.getId());
    }

    private static UserResponse setupUser(UserService userService) {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("김용준");
        request.setEmail("kim@test.com");
        request.setPhone("010-1234-5678");
        request.setPassword("secret");
        UserResponse user = userService.create(request);
        System.out.println("User created: " + user.getId());
        return user;
    }

    private static com.sprint.mission.discodeit.dto.channel.ChannelResponse setupChannel(ChannelService channelService) {
        ChannelCreateRequest request = new ChannelCreateRequest();
        request.setName("general");
        request.setDescription("public channel");
        var channel = channelService.createPublic(request);
        System.out.println("Channel created: " + channel.getId());
        return channel;
    }

    private static void messageCreateTest(
            MessageService messageService,
            java.util.UUID channelId,
            java.util.UUID userId
    ) {
        MessageCreateRequest request = new MessageCreateRequest();
        request.setUserId(userId);
        request.setChannelId(channelId);
        request.setContent("hello discodeit");
        messageService.create(request);
        System.out.println("Message created");
    }
}
