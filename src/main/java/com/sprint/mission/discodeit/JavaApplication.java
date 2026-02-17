package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        UserRepository userRepository = new JCFUserRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();
        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        UserService userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
        ChannelService channelService = new BasicChannelService(
                channelRepository,
                userRepository,
                readStatusRepository,
                messageRepository,
                binaryContentRepository
        );
        MessageService messageService = new BasicMessageService(
                messageRepository,
                channelRepository,
                userRepository,
                binaryContentRepository
        );

        UserCreateRequest userRequest = new UserCreateRequest();
        userRequest.setUsername("김용준");
        userRequest.setEmail("kim@example.com");
        userRequest.setPhone("010-1111-1111");
        userRequest.setPassword("secret");
        UUID userId = userService.create(userRequest).getId();

        ChannelCreateRequest publicChannel = new ChannelCreateRequest();
        publicChannel.setName("general");
        publicChannel.setDescription("public channel");
        UUID channelId = channelService.createPublic(publicChannel).getId();

        PrivateChannelCreateRequest privateChannel = new PrivateChannelCreateRequest();
        privateChannel.setParticipantUserIds(List.of(userId));
        channelService.createPrivate(privateChannel);

        MessageCreateRequest messageRequest = new MessageCreateRequest();
        messageRequest.setContent("안녕하세요");
        messageRequest.setUserId(userId);
        messageRequest.setChannelId(channelId);
        messageService.create(messageRequest);
    }
}
