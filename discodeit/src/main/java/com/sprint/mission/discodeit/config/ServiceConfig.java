package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService(
            UserRepository userRepository,
            UserStatusRepository userStatusRepository,
            BinaryContentRepository binaryContentRepository
    ) {
        return new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
    }

    @Bean
    public ChannelService channelService(
            ChannelRepository channelRepository,
            MessageRepository messageRepository,
            ReadStatusRepository readStatusRepository
    ) {
        return new BasicChannelService(channelRepository, messageRepository, readStatusRepository);
    }

    @Bean
    public MessageService messageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChannelRepository channelRepository,
            BinaryContentRepository binaryContentRepository
    ) {
        return new BasicMessageService(messageRepository, userRepository, channelRepository, binaryContentRepository);
    }

    @Bean
    public UserStatusService userStatusService(
            UserStatusRepository userStatusRepository,
            UserRepository userRepository
    ) {
        return new BasicUserStatusService(userStatusRepository, userRepository);
    }

    @Bean
    public ReadStatusService readStatusService(
            ReadStatusRepository readStatusRepository,
            ChannelRepository channelRepository,
            UserRepository userRepository
    ) {
        return new BasicReadStatusService(readStatusRepository, channelRepository, userRepository);
    }

    @Bean
    public BinaryContentService binaryContentService(
            BinaryContentRepository binaryContentRepository
    ) {
        return new BasicBinaryContentService(binaryContentRepository);
    }
}
