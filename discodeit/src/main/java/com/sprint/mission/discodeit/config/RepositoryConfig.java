package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Value("${discodeit.repository.file-directory}")
    private String fileDirectory;
    // --- User ---

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
    public UserRepository jcfUserRepository() {
        return new JCFUserRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public UserRepository fileUserRepository() {
        return new FileUserRepository(fileDirectory + "/users.txt");
    }

    // --- Channel ---
    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
    public ChannelRepository jcfChannelRepository() {
        return new JCFChannelRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public ChannelRepository fileChannelRepository() {
        return new FileChannelRepository(fileDirectory + "/channels.txt");
    }

    // --- Message ---
    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
    public MessageRepository jcfMessageRepository() {
        return new JCFMessageRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public MessageRepository fileMessageRepository() {
        return new FileMessageRepository(fileDirectory + "/messages.txt");
    }

    // --- UserStatus ---
    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
    public UserStatusRepository jcfUserStatusRepository() {
        return new JCFUserStatusRepository();
    }
    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public UserStatusRepository fileUserStatusRepository() {
        return new FileUserStatusRepository(fileDirectory + "/user-status.txt");
    }

    // --- ReadStatus ---
    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
    public ReadStatusRepository jcfReadStatusRepository() {
        return new JCFReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public ReadStatusRepository fileReadStatusRepository() {
        return new FileReadStatusRepository(fileDirectory + "/read-status.txt");
    }

    // --- BinaryContent ---
    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
    public BinaryContentRepository jcfBinaryContentRepository() {
        return new JCFBinaryContentRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public BinaryContentRepository fileBinaryContentRepository() {
        return new FileBinaryContentRepository(fileDirectory + "/binary-contents.txt");
    }
}