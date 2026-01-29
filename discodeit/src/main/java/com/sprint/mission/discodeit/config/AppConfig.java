package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${discodeit.repository.file-directory:.discodeit}")
    private String fileDirectory;

    // =========================
    // UserRepository
    // =========================
    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public UserRepository fileUserRepository() {
        return new FileUserRepository(fileDirectory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public UserRepository jcfUserRepository() {
        return new JCFUserRepository();
    }

    // =========================
    // ChannelRepository
    // =========================
    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public ChannelRepository fileChannelRepository() {
        return new FileChannelRepository(fileDirectory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public ChannelRepository jcfChannelRepository() {
        return new JCFChannelRepository();
    }

    // =========================
    // MessageRepository
    // =========================
    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public MessageRepository fileMessageRepository() {
        return new FileMessageRepository(fileDirectory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public MessageRepository jcfMessageRepository() {
        return new JCFMessageRepository();
    }

    // =========================
    // 이미 있던 JCF 구현체들(그대로 둠)
    // =========================
    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new JCFReadStatusRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new JCFBinaryContentRepository();
    }

    @Bean
    public MessageAttachmentRepository messageAttachmentRepository() {
        return new JCFMessageAttachmentRepository();
    }
}



