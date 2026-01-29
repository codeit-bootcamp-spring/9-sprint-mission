package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.repository.file.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    // [그룹 1] JCF 모드일 때 활성화되는 설정
    @Configuration
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
    public static class JcfRepositoryConfig {

        @Bean
        public UserRepository userRepository() {
            return new JCFUserRepository();
        }

        @Bean
        public ChannelRepository channelRepository() {
            return new JCFChannelRepository();
        }

        @Bean
        public MessageRepository messageRepository() {
            return new JCFMessageRepository();
        }

        @Bean
        public UserStatusRepository userStatusRepository() {
            return new JCFUserStatusRepository();
        }

        @Bean
        public ReadStatusRepository readStatusRepository() {
            return new JCFReadStatusRepository();
        }

        @Bean
        public BinaryContentRepository binaryContentRepository() {
            return new JCFBinaryContentRepository();
        }
    }

    // [그룹 2] File 모드일 때 활성화되는 설정
    @Configuration
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public static class FileRepositoryConfig {

        @Value("${discodeit.repository.file-directory}")
        private String fileDirectory; // 설정 파일에서 경로 가져오기

        @Bean
        public UserRepository userRepository() {
            return new FileUserRepository(fileDirectory);
        }

        @Bean
        public ChannelRepository channelRepository() {
            return new FileChannelRepository(fileDirectory); // 경로 전달!
        }

        @Bean
        public MessageRepository messageRepository() {
            return new FileMessageRepository(fileDirectory); // 경로 전달!
        }
    }
}