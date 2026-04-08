package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("유저가 참여 중인 채널 또는 공개 채널 목록 조회 쿼리 검증")
  void findAllByUserIdOrPublic_Success() {

    User user = entityManager.persist(new User("user1", "u1@t.com", "pass", null));
    User other = entityManager.persist(new User("user2", "u2@t.com", "pass", null));

    entityManager.persist(new Channel("Public", "Desc", ChannelType.PUBLIC, other));
    Channel pri = new Channel("Private", "Desc", ChannelType.PRIVATE, other);
    pri.addParticipant(user);
    entityManager.persist(pri);

    List<Channel> results = channelRepository.findAllByUserIdOrPublic(user.getId());

    assertThat(results).hasSize(2);
    assertThat(results).extracting(Channel::getName).containsExactlyInAnyOrder("Public", "Private");
  }

  @Test
  @DisplayName("참여 중인 채널이 없는 유저가 조회 시 공개 채널만 반환")
  void findAllByUserIdOrPublic_OnlyPublic() {

    User userWithoutChannels = entityManager.persist(new User("newbie", "new@t.com", "pass", null));
    User other = entityManager.persist(new User("user2", "u2@t.com", "pass", null));

    entityManager.persist(new Channel("Public", "Desc", ChannelType.PUBLIC, other));
    entityManager.persist(new Channel("Secret", "Desc", ChannelType.PRIVATE, other));

    List<Channel> results = channelRepository.findAllByUserIdOrPublic(userWithoutChannels.getId());

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Public");
  }

  @Test
  @DisplayName("특정 유저가 참여 중인 프라이빗 채널만 조회 검증")
  void findAllByUserId_Success() {

    User user = entityManager.persist(new User("member", "m@t.com", "pass", null));
    User owner = entityManager.persist(new User("owner", "o@t.com", "pass", null));

    Channel myChannel = new Channel("My Private", "Desc", ChannelType.PRIVATE, owner);
    myChannel.addParticipant(user);
    entityManager.persist(myChannel);

    List<Channel> results = channelRepository.findAllByUserId(user.getId());

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("My Private");
  }
}