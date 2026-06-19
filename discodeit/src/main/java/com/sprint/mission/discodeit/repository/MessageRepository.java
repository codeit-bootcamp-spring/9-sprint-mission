package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT m FROM Message m "
      + "WHERE m.channel.id = :channelId "
      + "ORDER BY m.createdAt DESC")
  Slice<Message> findByChannelId(
      @Param("channelId") UUID channelId,
      Pageable pageable
  );

  @Query("SELECT m FROM Message m "
      + "WHERE m.channel.id = :channelId "
      + "AND m.createdAt < :cursor "
      + "ORDER BY m.createdAt DESC")
  Slice<Message> findByChannelIdBeforeCursor(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable
  );

  @Query("SELECT DISTINCT m FROM Message m "
      + "LEFT JOIN FETCH m.author "
      + "LEFT JOIN FETCH m.attachments a "
      + "LEFT JOIN FETCH a.attachment "
      + "WHERE m.id = :messageId")
  Optional<Message> findByIdWithDetails(@Param("messageId") UUID messageId);

  boolean existsByIdAndAuthor_Id(UUID messageId, UUID authorId);

  void deleteAllByChannel_Id(UUID channelId);
}
