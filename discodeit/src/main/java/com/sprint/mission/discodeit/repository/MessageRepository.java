package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Message save(Message message);

  Optional<Message> findById(UUID id);

  List<Message> findAllByChannelId(UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);

  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);


  @Modifying
  @Query(value = "DELETE FROM binary_contents WHERE message_id IN (SELECT id FROM messages WHERE channel_id = :channelId)", nativeQuery = true)
  void deleteAttachmentsByChannelId(@Param("channelId") UUID channelId);

  @Modifying
  @Query(value = "DELETE FROM messages WHERE channel_id = :channelId", nativeQuery = true)
  void deleteMessagesByChannelId(@Param("channelId") UUID channelId);
}
