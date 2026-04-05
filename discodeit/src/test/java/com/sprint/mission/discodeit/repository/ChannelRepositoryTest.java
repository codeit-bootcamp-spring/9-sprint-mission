package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager entityManager;

  private User user1;
  private Channel publicChannel1;
  private Channel privateChannel1;

  @BeforeEach
  void setUp() {
    user1 = new User("user1", "user1@email.com", "password", null);
    entityManager.persist(user1);

    publicChannel1 = new Channel(ChannelType.PUBLIC, "Public Channel 1", "Desc1");
    entityManager.persist(publicChannel1);

    Channel publicChannel2 = new Channel(ChannelType.PUBLIC, "Public Channel 2", "Desc2");
    entityManager.persist(publicChannel2);

    privateChannel1 = new Channel(ChannelType.PRIVATE, null, null);
    entityManager.persist(privateChannel1);

    Channel privateChannel2 = new Channel(ChannelType.PRIVATE, null, null);
    entityManager.persist(privateChannel2);

    // user1을 privateChannel1에 참여시킴
    ReadStatus readStatus = new ReadStatus(user1, privateChannel1, null);
    entityManager.persist(readStatus);
  }

  @Test
  @DisplayName("findAllByTypeOrIdIn (커스텀 쿼리) - 성공")
  void findAllByTypeOrIdIn_Success() {
    // given
    // 모든 PUBLIC 채널과, 내가 참여한 PRIVATE 채널(privateChannel1)의 ID
    List<UUID> mySubscribedChannelIds = List.of(privateChannel1.getId());

    // when
    // PUBLIC 타입이거나, ID가 mySubscribedChannelIds 리스트에 포함된 채널 조회
    List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds);

    // then
    // Public 2개 + Private 1개 = 총 3개 채널이 조회되어야 함
    assertThat(channels).hasSize(3);
    assertThat(channels).extracting(Channel::getName)
        .contains("Public Channel 1", "Public Channel 2", null);
  }

  @Test
  @DisplayName("findAllByTypeOrIdIn (커스텀 쿼리) - 실패 (조건에 맞는 채널 없음)")
  void findAllByTypeOrIdIn_Fail() {
    // given
    // 존재하지 않는 채널 ID
    List<UUID> nonExistentIds = List.of(UUID.randomUUID());

    // when
    // PUBLIC 채널은 존재하지만, 테스트를 위해 PUBLIC이 아닌 타입과 존재하지 않는 ID로 조회
    List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PRIVATE, nonExistentIds);

    // then
    // privateChannel1, privateChannel2는 ID가 nonExistentIds에 없으므로 조회되지 않아야 함
    assertThat(channels).isEmpty();
  }
}
