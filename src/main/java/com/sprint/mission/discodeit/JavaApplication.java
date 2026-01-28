package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {

    static void runAllTests(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        User woody = userService.create("woody", "woody@codeit.com", "1234");
        User buzz = userService.create("buzz", "buzz@codeit.com", "5678");

        Channel notice = channelService.create("공지", ChannelType.PUBLIC);
        Channel chat = channelService.create("잡담", ChannelType.PUBLIC);

        messageService.create(notice.getId(), buzz.getId(), "반갑습니다");
        messageService.create(chat.getId(), woody.getId(), "잡담 시작");
        messageService.create(notice.getId(), woody.getId(), "안녕하세요");

        messageService.findAll().forEach(System.out::println);
    }

    public static void main(String[] args) {

        System.out.println("===== JCF Repository Test =====");

        UserRepository jcfUserRepository = new JCFUserRepository();
        ChannelRepository jcfChannelRepository = new JCFChannelRepository();
        MessageRepository jcfMessageRepository = new JCFMessageRepository();

        UserService jcfUserService =
                new BasicUserService(jcfUserRepository);
        ChannelService jcfChannelService =
                new BasicChannelService(jcfChannelRepository);
        MessageService jcfMessageService =
                new BasicMessageService(
                        jcfUserService,
                        jcfChannelService,
                        jcfMessageRepository
                );

        runAllTests(jcfUserService, jcfChannelService, jcfMessageService);

        System.out.println();
        System.out.println("===== File Repository Test =====");

        UserRepository fileUserRepository =
                new FileUserRepository("file-data/user");
        ChannelRepository fileChannelRepository =
                new FileChannelRepository("file-data/channel");
        MessageRepository fileMessageRepository =
                new FileMessageRepository("file-data/message");

        UserService fileUserService =
                new BasicUserService(fileUserRepository);
        ChannelService fileChannelService =
                new BasicChannelService(fileChannelRepository);
        MessageService fileMessageService =
                new BasicMessageService(
                        fileUserService,
                        fileChannelService,
                        fileMessageRepository
                );

        runAllTests(fileUserService, fileChannelService, fileMessageService);
    }
}
