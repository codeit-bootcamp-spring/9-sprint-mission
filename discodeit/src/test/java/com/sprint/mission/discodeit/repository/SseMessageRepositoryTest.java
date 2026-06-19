package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.sse.SseMessage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SseMessageRepositoryTest {

  private final SseMessageRepository repository = new SseMessageRepository();

  @Test
  @DisplayName("findAllAfter: LastEventId 이후 수신 가능한 이벤트만 복원한다")
  void findAllAfter_returnsReceivableMessagesAfterLastEventId() {
    UUID receiverId = UUID.randomUUID();
    UUID otherReceiverId = UUID.randomUUID();
    SseMessage first = repository.save(List.of(receiverId), "notifications.created", "first");
    SseMessage second = repository.save(List.of(otherReceiverId), "notifications.created", "other");
    SseMessage third = repository.save(List.of(), "users.updated", "broadcast");

    List<SseMessage> result = repository.findAllAfter(first.id(), receiverId);

    assertThat(result).containsExactly(third);
    assertThat(result).doesNotContain(second);
  }

  @Test
  @DisplayName("findAllAfter: LastEventId가 없으면 첫 연결로 보고 과거 이벤트를 복원하지 않는다")
  void findAllAfter_withoutLastEventId_returnsEmpty() {
    UUID receiverId = UUID.randomUUID();
    repository.save(List.of(receiverId), "notifications.created", "first");

    List<SseMessage> result = repository.findAllAfter(null, receiverId);

    assertThat(result).isEmpty();
  }
}
