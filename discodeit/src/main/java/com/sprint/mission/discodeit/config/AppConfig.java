package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;


// ✅ 인터페이스는 repository 바로 아래
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

// ✅ 구현체는 repository.file 아래
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;

// ✅ 인터페이스는 service 바로 아래
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

// ✅ 구현체는 service.basic 아래
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

@Configuration
public class AppConfig {

    @Bean
    public AuthService authService(UserRepository userRepository) {
        return new BasicAuthService(userRepository);
    }

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository();
    }

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new BasicUserService(userRepository);
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new JCFReadStatusRepository(); // 일단 JCF로 고정
    }

    @Bean
    public ChannelService channelService(
            ChannelRepository channelRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ReadStatusRepository readStatusRepository
    ) {
        return new BasicChannelService(
                channelRepository,
                messageRepository,
                userRepository,
                readStatusRepository
        );
    }


    @Bean
    public MessageService messageService(
            MessageRepository messageRepository,
            ChannelRepository channelRepository,
            UserRepository userRepository
    ) {
        return new BasicMessageService(messageRepository, channelRepository, userRepository);
    }
}


