package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author", "author.profile", "channel"})
  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);


  @Query("SELECT MAX(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

  @EntityGraph(attributePaths = {"author", "author.profile", "channel"})
  @Query("SELECT m FROM Message m " +
      "WHERE m.channel.id = :channelId " +
      "AND (cast(:cursor as timestamp ) IS NULL OR m.createdAt < :cursor)")
  Slice<Message> findByChannelIdAndCreatedAtBefore(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable);

  void deleteAllByChannelId(UUID channelId);

  @Modifying
  @Query("delete from Message m where m.author.id = :userId ")
  void deleteByUserId(@Param("userId") UUID userId);

  boolean existsByChannelId(UUID channelId);
}
