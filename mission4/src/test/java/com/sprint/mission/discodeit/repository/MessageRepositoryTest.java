package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.within;
import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.config.JpaAuditConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.validator.cfg.defs.UUIDDef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;


@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private EntityManager em;

  @Test
  void findLastMessageAtByChannelId() {
    Channel channel = Channel.builder().name("갱갱").description("갱갱").type(ChannelType.PRIVATE)
        .build();
    channelRepository.save(channel);
    Message m1 = Message.builder().channel(channel).content("hi").build();
    messageRepository.save(m1);
    Message m2 = Message.builder().channel(channel).content("bye").build();
    messageRepository.save(m2);

    Optional<Instant> lastTime = messageRepository.findLastMessageAtByChannelId(channel.getId());

    assertThat(lastTime).isPresent();
    assertThat(lastTime.get()).isCloseTo(m2.getCreatedAt(),
        within(100, ChronoUnit.MILLIS));


  }

  @Test
  @DisplayName("채널 ID와 특정 시각을 기준으로 이전 메시지 목록을 조회한다")
  void findByChannelIdAndCreatedAtBefore() {
    Channel channel = channelRepository.save(
        Channel.builder().name("테스트채널").type(ChannelType.PUBLIC).build());

    Message m1 = messageRepository.save(Message.builder().channel(channel).content("old").build());
    Message m2 = messageRepository.save(
        Message.builder().channel(channel).content("middle").build());
    Message m3 = messageRepository.save(Message.builder().channel(channel).content("new").build());

    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
    Slice<Message> firstPage = messageRepository.findByChannelIdAndCreatedAtBefore(channel.getId(),
        null, pageable);

    assertThat(firstPage.getContent()).hasSize(2);
    assertThat(firstPage.getContent().get(0).getContent()).isEqualTo("new");
    assertThat(firstPage.hasNext()).isTrue();

    Instant cursor = m3.getCreatedAt();
    Slice<Message> secondPage = messageRepository.findByChannelIdAndCreatedAtBefore(channel.getId(),
        cursor, pageable);

    assertThat(secondPage.getContent()).extracting("content")
        .containsExactly("middle", "old");
    assertThat(secondPage.hasNext()).isFalse();
  }

}
