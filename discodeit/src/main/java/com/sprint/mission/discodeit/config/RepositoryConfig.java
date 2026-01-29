package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

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
        return new UserStatusRepository() {
            private final Map<UUID, UserStatus> store = new ConcurrentHashMap<>();

            @Override
            public UserStatus save(UserStatus userStatus) {
                if (userStatus == null) throw new IllegalArgumentException("userStatus is null");
                store.put(userStatus.getId(), userStatus);
                return userStatus;
            }

            @Override
            public Optional<UserStatus> findById(UUID id) {
                if (id == null) return Optional.empty();
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public Optional<UserStatus> findByUserId(UUID userId) {
                if (userId == null) return Optional.empty();
                return store.values().stream()
                        .filter(s -> userId.equals(s.getUserId()))
                        .findFirst();
            }

            @Override
            public List<UserStatus> findAll() {
                return new ArrayList<>(store.values());
            }

            @Override
            public void deleteById(UUID id) {
                if (id == null) return;
                store.remove(id);
            }

            @Override
            public boolean existsById(UUID id) {
                if (id == null) return false;
                return store.containsKey(id);
            }
        };
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new BinaryContentRepository() {
            private final Map<UUID, BinaryContent> store = new ConcurrentHashMap<>();

            @Override
            public BinaryContent save(BinaryContent binaryContent) {
                if (binaryContent == null) throw new IllegalArgumentException("binaryContent is null");
                store.put(binaryContent.getId(), binaryContent);
                return binaryContent;
            }

            @Override
            public Optional<BinaryContent> findById(UUID id) {
                if (id == null) return Optional.empty();
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public List<BinaryContent> findAll() {
                return new ArrayList<>(store.values());
            }

            @Override
            public void deleteById(UUID id) {
                if (id == null) return;
                store.remove(id);
            }

            @Override
            public boolean existsById(UUID id) {
                if (id == null) return false;
                return store.containsKey(id);
            }
        };
    }
}
