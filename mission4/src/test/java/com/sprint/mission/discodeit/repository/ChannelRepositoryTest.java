package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository repository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private EntityManager em;


  @Test
  @DisplayName("UserId로 유저가 속한 채널 조회 테스트->@Query적용")
  void findAllAccessibleByUserId() {
    User user = User.builder().username("승현").email("sueng@naver.com").password("1234").build();
    userRepository.save(user);
    User otherUser = User.builder().username("콘").email("corn@naver.com").password("3213").build();
    userRepository.save(otherUser);
    Channel channel = Channel.builder().name("갱").type(ChannelType.PUBLIC).description("갱갱")
        .build();
    repository.save(channel);
    Channel privateChannel = Channel.builder()
        .name("gang")
        .type(ChannelType.PRIVATE)
        .build();
    repository.save(privateChannel);

    ReadStatus status = ReadStatus.builder()
        .user(user)
        .channel(privateChannel)
        .lastReadAt(Instant.now())
        .build();
    readStatusRepository.save(status);
    ReadStatus otherStatus = ReadStatus.builder()
        .user(otherUser)
        .channel(privateChannel).
        lastReadAt(Instant.now())
        .build();
    readStatusRepository.save(otherStatus);

    em.flush();
    em.clear();

    List<Channel> channelList = repository.findAllAccessibleByUserId(user.getId());
    assertThat(channelList)
        .isNotEmpty().hasSize(2).extracting("name").containsExactlyInAnyOrder("gang", "갱");
  }

  @Test
  @DisplayName("전체 체널 조회 테스트->Entitygraph사용")
  void findAll() {
    Channel channel = Channel.builder().name("갱갱").description("갱갱갱").type(ChannelType.PUBLIC)
        .build();
    repository.save(channel);
    Channel privateChannel = Channel.builder().name("시크릿").description("시크릿")
        .type(ChannelType.PRIVATE).build();
    repository.save(privateChannel);

    em.flush();
    em.clear();
    List<Channel> channels =
        repository.findAll();
    assertThat(channels).isNotEmpty()
        .hasSize(2)
        .extracting("name")
        .containsExactly("갱갱", "시크릿");

  }
}