package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.PersistenceUnitUtil;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryDataJpaTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Autowired
  private EntityManager entityManager;

  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;

  @Test
  @DisplayName("findByIdWithDetails 성공: author와 attachment를 함께 조회한다")
  void findByIdWithDetails_success() {
    User author = userRepository.save(new User("jun", "jun@test.com", "password123", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    BinaryContent attachment = binaryContentRepository.save(new BinaryContent("a.png", 10L, "image/png"));

    Message message = messageRepository.saveAndFlush(new Message("hello", channel, author));
    message.addAttachment(attachment);
    messageRepository.saveAndFlush(message);
    entityManager.clear();

    Optional<Message> found = messageRepository.findByIdWithDetails(message.getId());

    assertTrue(found.isPresent());
    assertEquals("hello", found.get().getContent());
    assertEquals(1, found.get().getAttachments().size());

    PersistenceUnitUtil unitUtil = entityManagerFactory.getPersistenceUnitUtil();
    assertTrue(unitUtil.isLoaded(found.get(), "author"));
    assertTrue(unitUtil.isLoaded(found.get(), "attachments"));
    assertTrue(unitUtil.isLoaded(found.get().getAttachments().get(0), "attachment"));
  }

  @Test
  @DisplayName("findByIdWithDetails 실패: 없는 메시지 ID면 빈 결과를 반환한다")
  void findByIdWithDetails_fail_notFound() {
    Optional<Message> found = messageRepository.findByIdWithDetails(UUID.randomUUID());

    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("findByChannelIdWithCursor 성공: 커서 없이 최신 메시지부터 페이징한다")
  void findByChannelIdWithCursor_success_withoutCursor() {
    User author = userRepository.save(new User("jun", "jun@test.com", "password123", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));

    messageRepository.save(new Message("first", channel, author));
    messageRepository.save(new Message("second", channel, author));
    messageRepository.save(new Message("third", channel, author));

    Instant cursor = Instant.now().plusSeconds(60);

    Slice<Message> firstSlice = messageRepository.findByChannelIdWithCursor(
        channel.getId(),
        cursor,
        PageRequest.of(0, 2)
    );

    assertEquals(2, firstSlice.getContent().size());
    assertTrue(firstSlice.hasNext());

    Message first = firstSlice.getContent().get(0);
    Message second = firstSlice.getContent().get(1);

    boolean sortedDesc = first.getCreatedAt().isAfter(second.getCreatedAt())
        || (first.getCreatedAt().equals(second.getCreatedAt())
        && first.getId().compareTo(second.getId()) > 0);
    assertTrue(sortedDesc);
  }

  @Test
  @DisplayName("findByChannelIdWithCursor 성공: 커서 이후에는 더 오래된 메시지만 조회한다")
  void findByChannelIdWithCursor_success_withCursor() throws InterruptedException {
    User author = userRepository.save(new User("jun2", "jun2@test.com", "password123", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));

    messageRepository.saveAndFlush(new Message("first", channel, author));
    Thread.sleep(10);
    messageRepository.saveAndFlush(new Message("second", channel, author));
    Thread.sleep(10);
    messageRepository.saveAndFlush(new Message("third", channel, author));

    Instant firstCursor = Instant.now().plusSeconds(60);

    Slice<Message> firstSlice = messageRepository.findByChannelIdWithCursor(
        channel.getId(),
        firstCursor,
        PageRequest.of(0, 2)
    );

    List<Message> firstSliceMessages = firstSlice.getContent();
    Message cursorMessage = firstSliceMessages.get(firstSliceMessages.size() - 1);

    Slice<Message> secondSlice = messageRepository.findByChannelIdWithCursor(
        channel.getId(),
        cursorMessage.getCreatedAt(),
        PageRequest.of(0, 2)
    );

    assertEquals(1, secondSlice.getContent().size());
    assertTrue(secondSlice.getContent().stream()
        .noneMatch(message -> firstSliceMessages.stream()
            .anyMatch(first -> first.getId().equals(message.getId()))));
  }

  @Test
  @DisplayName("findByChannelIdWithCursor 실패: 채널 데이터가 없으면 빈 슬라이스를 반환한다")
  void findByChannelIdWithCursor_fail_noChannelMessages() {
    Slice<Message> slice = messageRepository.findByChannelIdWithCursor(
        UUID.randomUUID(),
        Instant.now(),
        PageRequest.of(0, 5)
    );

    assertTrue(slice.getContent().isEmpty());
    assertTrue(slice.isLast());
  }
}

