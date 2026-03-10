package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message, UUID> {

    @Query("""
        SELECT DISTINCT m
        FROM Message m
        LEFT JOIN FETCH m.author a
        LEFT JOIN FETCH a.status
        LEFT JOIN FETCH m.attachments att
        WHERE m.channel.id = :channelId
        ORDER BY m.createdAt DESC, m.id DESC
    """)
    List<Message> findAllWithAttachmentsAndAuthorByChannel(@Param("channelId") UUID channelId);

    @Query("""
        SELECT m
        FROM Message m
        WHERE m.channel.id = :channelId
        ORDER BY m.createdAt DESC, m.id DESC
    """)
    Slice<Message> findByChannel_Id(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.status", "attachments"})
    @Query("""
    SELECT m
    FROM Message m
    WHERE m.channel.id = :channelId
    ORDER BY m.createdAt DESC, m.id DESC
""")
    List<Message> findAllByChannelOrdered(@Param("channelId") UUID channelId);

    void deleteByChannel_Id(UUID channelId);

    @Query("""
        SELECT MAX(m.createdAt)
        FROM Message m
        WHERE m.channel.id = :channelId
    """)
    Optional<Instant> findLatestMessageTime(@Param("channelId") UUID channelId);



    @EntityGraph(attributePaths = {"author", "author.status"})
    @Query("""
SELECT m
FROM Message m
WHERE m.channel.id = :channelId
ORDER BY m.createdAt DESC, m.id DESC
""")
    Slice<Message> findFirstMessages(
        @Param("channelId") UUID channelId,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"author", "author.status"})
    @Query("""
SELECT m
FROM Message m
WHERE m.channel.id = :channelId
AND (
     m.createdAt < :cursorCreatedAt
     OR (m.createdAt = :cursorCreatedAt AND m.id < :cursorId)
)
ORDER BY m.createdAt DESC, m.id DESC
""")
    Slice<Message> findMessagesByCursor(
        @Param("channelId") UUID channelId,
        @Param("cursorCreatedAt") Instant cursorCreatedAt,
        @Param("cursorId") UUID cursorId,
        Pageable pageable
    );
}