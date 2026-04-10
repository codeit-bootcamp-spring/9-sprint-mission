package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditConfig;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TestEntityManager em;

  private User savedUser;

  @BeforeEach
  void setup() {
    User user = User.builder()
        .username("승현")
        .email("seung@naver.com")
        .password("1234")
        .build();
    this.savedUser = userRepository.save(user);
    em.flush();
    em.clear();
  }


  @Test
  @DisplayName("성공: EntityGraph를 이용한 유저 단건 조회 ")
  void findById_Success() {

    Optional<User> found = userRepository.findById(savedUser.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("seung@naver.com");

  }

  @Test
  @DisplayName("성공: EntityGraph를 이용한 유저 전체 조회")
  void findAll_Success() {
    List<User> found = userRepository.findAll();
    assertThat(found).isNotEmpty()
        .hasSize(1)
        .extracting("username")
        .containsExactly("승현");


  }
}