package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant; // 💡 추가
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 💡 추가
import org.springframework.data.repository.query.Param; // 💡 추가

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  @Query("select max(m.createdAt) from Message m where m.channel.id = :channelId")
  Instant findLastMessageCreatedAtByChannelId(@Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}