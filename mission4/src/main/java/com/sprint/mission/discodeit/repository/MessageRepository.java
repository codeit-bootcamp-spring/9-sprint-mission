package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author", "author.profile", "author.status", "channel"})
  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

//  @EntityGraph(attributePaths = {"author", "author.profile", "author.status"})
//  Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);


  @EntityGraph(attributePaths = {"author", "author.profile", "author.status", "channel"})
  @Query("SELECT m FROM Message m " +
      "WHERE m.channel.id = :channelId " +
      "AND (CAST(:cursor AS localdatetime) IS NULL OR m.createdAt < :cursor)")
    // CAST 추가
  Slice<Message> findByChannelIdAndCreatedAtBefore(
      @Param("channelId") UUID channelId,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable);

  void deleteAllByChannelId(UUID channelId);
}
