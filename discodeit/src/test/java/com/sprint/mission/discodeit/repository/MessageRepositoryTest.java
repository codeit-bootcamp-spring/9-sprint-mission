package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MessageRepository 슬라이스 테스트")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("채널 내 메시지 조회 (커서 기반 Slice) - 성공")
  void findOlderByChannelId_SliceSuccess() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant now = Instant.now();

    // 주의: 실제 환경에서는 Channel과 User(Author) 엔티티가 먼저 persist 되어야 함
    // 여기서는 쿼리 구문 및 파라미터 바인딩 동작 여부를 검증함

    PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    // when
    Slice<Message> result = messageRepository.findOlderByChannelId(channelId, now, pageable);

    // then
    assertThat(result).isNotNull();
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  @DisplayName("채널별 마지막 메시지 시간 프로젝션 조회 - 결과 없음 확인")
  void findLastMessageAtByChannelIds_EmptyResult() {
    // given
    List<UUID> channelIds = List.of(UUID.randomUUID());

    // when
    List<MessageAtProjection> projections = messageRepository.findLastMessageAtByChannelIds(
        channelIds);

    // then
    assertThat(projections).isEmpty();
  }
}