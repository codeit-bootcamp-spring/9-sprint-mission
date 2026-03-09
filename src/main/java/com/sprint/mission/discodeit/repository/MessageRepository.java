package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message, UUID> {

    @Query("""
        SELECT DISTINCT m
        FROM Message m
        LEFT JOIN FETCH m.author a
        LEFT JOIN FETCH a.status
        LEFT JOIN FETCH m.attachments att
        WHERE m.channel.id = :channelId
        ORDER BY m.createdAt DESC
    """)
    List<Message> findAllWithAttachmentsAndAuthorByChannel(@Param("channelId") UUID channelId);

    @EntityGraph(attributePaths = {"author", "author.status", "attachments"})
    Slice<Message> findByChannel_Id(UUID channelId, Pageable pageable);

    void deleteByChannel_Id(UUID channelId);

    @Query("""
    SELECT MAX(m.createdAt)
    FROM Message m
    WHERE m.channel.id = :channelId
""")
    Optional<Instant> findLatestMessageTime(@Param("channelId") UUID channelId);
}