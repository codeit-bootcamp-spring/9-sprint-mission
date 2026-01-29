package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

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
    public ChannelService channelService(ChannelRepository channelRepository) {
        return new BasicChannelService(channelRepository);
    }

    @Bean
    public MessageService messageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChannelRepository channelRepository) {
        return new BasicMessageService(messageRepository, userRepository, channelRepository);
    }
}
