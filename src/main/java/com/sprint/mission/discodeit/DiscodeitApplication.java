package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    static void runAllTests(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        UserResponse woody = userService.create(
                new UserCreateRequest("woody2", "woody2@codeit.com", "1234", null)
        );
        UserResponse buzz = userService.create(
                new UserCreateRequest("buzz2", "buzz2@codeit.com", "5678", null)
        );

        ChannelResponse notice = channelService.createPublic(
                new CreatePublicChannelRequest("공지", null)
        );
        ChannelResponse chat = channelService.createPublic(
                new CreatePublicChannelRequest("잡담", null)
        );

        messageService.create(new MessageCreateRequest(
                notice.id(), buzz.id(), "반갑습니다", null
        ));
        messageService.create(new MessageCreateRequest(
                chat.id(), woody.id(), "잡담 시작", null
        ));
        messageService.create(new MessageCreateRequest(
                notice.id(), woody.id(), "안녕하세요", null
        ));

        messageService.findAllByChannelId(notice.id()).forEach(System.out::println);
        messageService.findAllByChannelId(chat.id()).forEach(System.out::println);
    }

    public static void main(String[] args) {
        System.out.println("===== JCF Repository Test =====");

        UserRepository jcfUser = new JCFUserRepository();
        UserStatusRepository jcfUserStatus = new JCFUserStatusRepository();
        BinaryContentRepository jcfBinary = new JCFBinaryContentRepository();
        ChannelRepository jcfChannel = new JCFChannelRepository();
        MessageRepository jcfMessage = new JCFMessageRepository();
        ReadStatusRepository jcfReadStatus = new JCFReadStatusRepository();

        UserService jcfUserService = new BasicUserService(jcfUser, jcfUserStatus, jcfBinary);
        ChannelService jcfChannelService = new BasicChannelService(jcfChannel, jcfMessage, jcfReadStatus);
        MessageService jcfMessageService = new BasicMessageService(
                jcfMessage, jcfBinary, jcfChannel, jcfUser
        );

        runAllTests(jcfUserService, jcfChannelService, jcfMessageService);

        System.out.println();
        System.out.println("===== File Repository Test =====");

        ConfigurableApplicationContext ctx =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = ctx.getBean(UserService.class);
        ChannelService channelService = ctx.getBean(ChannelService.class);
        MessageService messageService = ctx.getBean(MessageService.class);

        runAllTests(userService, channelService, messageService);
    }
}
