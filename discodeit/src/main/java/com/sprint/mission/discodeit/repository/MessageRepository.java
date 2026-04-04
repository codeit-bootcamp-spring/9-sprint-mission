package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message,UUID> {

  @Query("SELECT m FROM Message m "
      + "WHERE m.channel.id = :channelId"
      + " AND m.createdAt < :cursor"
      + " ORDER BY m.createdAt DESC")
  Page<Message> findAllByChannelId(@Param("channelId") UUID channelId, @Param("cursor")Instant cursor ,Pageable pageable);

  Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
