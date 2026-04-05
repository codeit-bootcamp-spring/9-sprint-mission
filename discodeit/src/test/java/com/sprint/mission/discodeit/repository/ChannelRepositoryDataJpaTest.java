package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.PersistenceUnitUtil;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChannelRepositoryDataJpaTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;

  @Test
  @DisplayName("findByIdWithMessages 성공: 채널과 메시지를 한 번에 조회한다")
  void findByIdWithMessages_success() {
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    User author = userRepository.save(new User("jun", "jun@test.com", "password123", null));
    messageRepository.save(new Message("hello", channel, author));
    messageRepository.save(new Message("world", channel, author));

    Optional<Channel> found = channelRepository.findByIdWithMessages(channel.getId());

    assertTrue(found.isPresent());
    assertEquals(2, found.get().getMessages().size());

    PersistenceUnitUtil unitUtil = entityManagerFactory.getPersistenceUnitUtil();
    assertTrue(unitUtil.isLoaded(found.get(), "messages"));
  }

  @Test
  @DisplayName("findByIdWithMessages 실패: 없는 채널 ID면 빈 결과를 반환한다")
  void findByIdWithMessages_fail_notFound() {
    Optional<Channel> found = channelRepository.findByIdWithMessages(UUID.randomUUID());

    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("findAll 페이징/정렬 성공: name 내림차순으로 페이지를 조회한다")
  void findAll_success_pagingAndSorting() {
    channelRepository.save(new Channel(ChannelType.PUBLIC, "alpha", "desc"));
    channelRepository.save(new Channel(ChannelType.PUBLIC, "gamma", "desc"));
    channelRepository.save(new Channel(ChannelType.PUBLIC, "beta", "desc"));

    Page<Channel> page = channelRepository.findAll(
        PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name")));

    assertEquals(2, page.getContent().size());
    assertEquals("gamma", page.getContent().get(0).getName());
    assertEquals("beta", page.getContent().get(1).getName());
    assertTrue(page.hasNext());
  }

  @Test
  @DisplayName("findAll 페이징/정렬 실패: 범위를 벗어난 페이지는 빈 결과를 반환한다")
  void findAll_fail_outOfRangePage() {
    channelRepository.save(new Channel(ChannelType.PUBLIC, "alpha", "desc"));

    Page<Channel> page = channelRepository.findAll(
        PageRequest.of(2, 1, Sort.by(Sort.Direction.ASC, "name")));

    assertTrue(page.getContent().isEmpty());
    assertEquals(1, page.getTotalElements());
  }
}

