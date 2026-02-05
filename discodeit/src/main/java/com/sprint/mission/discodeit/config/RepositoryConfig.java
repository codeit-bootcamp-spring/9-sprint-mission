package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RepositoryConfig {

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
    public UserStatusRepository userStatusRepository() {
        return new FileUserStatusRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository();
    }
}
