package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {"author", "attachments"})
    Slice<Message> findAllByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "attachments"})
    @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId AND m.createdAt < (SELECT m2.createdAt FROM Message m2 WHERE m2.id = :cursorId) ORDER BY m.createdAt DESC")
    Slice<Message> findAllByChannelIdAndCursorOrderByCreatedAtDesc(@Param("channelId") UUID channelId, @Param("cursorId") UUID cursorId, Pageable pageable);


    void deleteAllByChannelId(UUID channelId);

    @Query("SELECT m FROM Message m "
            + "LEFT JOIN FETCH m.author a "
            + "JOIN FETCH a.status "
            + "LEFT JOIN FETCH a.profile "
            + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
    Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
                                                @Param("createdAt") Instant createdAt,
                                                Pageable pageable);


    @Query("SELECT m.createdAt "
            + "FROM Message m "
            + "WHERE m.channel.id = :channelId "
            + "ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);
}