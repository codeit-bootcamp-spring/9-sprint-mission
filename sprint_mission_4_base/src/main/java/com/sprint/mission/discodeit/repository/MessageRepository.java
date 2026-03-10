package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author"})
  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  @EntityGraph(attributePaths = {"author"})
  @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId AND m.id < :cursor ORDER BY m.id DESC")
  Slice<Message> findByCursor(
      @Param("channelId") UUID channelId,
      @Param("cursor") UUID cursor,
      Pageable pageable
  );

  void deleteAllByChannelId(UUID channelId);

}