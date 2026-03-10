package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);


  @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId " +
      "AND m.createdAt < (SELECT c.createdAt FROM Message c WHERE c.id = :cursor) " +
      "ORDER BY m.createdAt DESC")
  Slice<Message> findByChannelIdBeforeCursor(
      @Param("channelId") UUID channelId,
      @Param("cursor") UUID cursor,
      Pageable pageable
  );

  @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId " +
      "ORDER BY m.createdAt DESC")
  Slice<Message> findByChannelIdOrderByCreatedAtDesc(
      @Param("channelId") UUID channelId,
      Pageable pageable
  );
}