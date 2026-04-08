package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.JpaAuditConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("메시지 저장 및 연관관계 확인 테스트")
  void save_message_and_check_relation() {
    // Given
    User author = userRepository.save(new User("작성자", "writer@test.com", "pass", null));
    Channel channel = channelRepository.save(new Channel("테스트채널", "설명", ChannelType.PUBLIC));

    // 메시지 생성 (내용, 채널, 작성자)
    Message message = new Message("반갑습니다!", channel, author);

    // When
    Message savedMessage = messageRepository.save(message);

    // Then
    assertThat(savedMessage.getId()).isNotNull();
    assertThat(savedMessage.getContent()).isEqualTo("반갑습니다!");
    assertThat(savedMessage.getAuthor().getUsername()).isEqualTo("작성자");
    assertThat(savedMessage.getChannel().getName()).isEqualTo("테스트채널");
  }

  @Test
  @DisplayName("커스텀 쿼리 테스트 - 채널별 메시지 페이징 조회")
  void find_messages_by_channel_paging() {
    // Given
    User author = userRepository.save(new User("user", "u@t.com", "p", null));
    Channel channel = channelRepository.save(new Channel("c", "d", ChannelType.PUBLIC));

    // 메시지 2개 저장
    messageRepository.save(new Message("첫 번째", channel, author));
    messageRepository.save(new Message("두 번째", channel, author));

    PageRequest pageRequest = PageRequest.of(0, 10);

    // When
    Slice<Message> result = messageRepository.findAllByChannel_Id(channel.getId(), pageRequest);

    // Then
    assertThat(result.getContent()).hasSize(2);
  }
}