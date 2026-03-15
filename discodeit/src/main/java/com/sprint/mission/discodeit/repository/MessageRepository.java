package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT DISTINCT m FROM Message m " +
      "JOIN FETCH m.author " +
      "WHERE m.channel.id = :channelId " +
      "AND (:lastMessageId IS NULL OR m.createdAt < (SELECT m2.createdAt FROM Message m2 WHERE m2.id = :lastMessageId)) "
      +
      "ORDER BY m.createdAt DESC")
  Slice<Message> findMessagesNoOffset(
      @Param("channelId") UUID channelId,
      @Param("lastMessageId") UUID lastMessageId,
      Pageable pageable);
}