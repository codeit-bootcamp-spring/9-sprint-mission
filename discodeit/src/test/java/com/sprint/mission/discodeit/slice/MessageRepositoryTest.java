package com.sprint.mission.discodeit.slice;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@Transactional
public class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ChannelRepository channelRepository;

  private User author;
  private Channel channel;

  @BeforeEach
  void setUp() {
    messageRepository.deleteAllInBatch();
    channelRepository.deleteAllInBatch();
    author = userRepository.save(new User("author", "pw", "author@test.com", null));
    channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
  }


  @Test
  @DisplayName("메시지 페이징 조회 성공")
  void findAllByChannel_Id_Success() throws InterruptedException {
    Message m1 = messageRepository.save(new Message(channel, author, "first", null));
    Thread.sleep(10);
    Message m2 = messageRepository.save(new Message(channel, author, "second", null));
    Thread.sleep(10);
    Message m3 = messageRepository.save(new Message(channel, author, "third", null));

    messageRepository.flush();

    PageRequest pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());
    List<Message> firstPage = messageRepository.findAllByChannel_Id(channel.getId(), null, pageable);

    assertThat(firstPage).hasSize(2);
    assertThat(firstPage.get(0).getContent()).isEqualTo("third");
    assertThat(firstPage.get(1).getContent()).isEqualTo("second");


    // 다음 페이지 확인
    Instant cursor = firstPage.get(1).getCreatedAt();
    List<Message> secondPage = messageRepository.findAllByChannel_Id(channel.getId(), cursor, pageable);

    assertThat(secondPage).hasSize(1);
    assertThat(secondPage.get(0).getContent()).isEqualTo("first");
  }

  @Test
  @DisplayName("존재하지 않는 채널 ID로 조회")
  void findAllByChannel_Id_Empty_WhenChannelNotFound() {
    UUID fakeChannelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);

    List<Message> result = messageRepository.findAllByChannel_Id(fakeChannelId, null, pageable);

    assertThat(result).isEmpty();
  }

}
