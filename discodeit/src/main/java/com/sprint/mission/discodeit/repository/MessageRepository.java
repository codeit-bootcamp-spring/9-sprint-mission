package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("select m from Message m " +
      "join fetch m.author a " +
      "left join fetch a.profile p " +
      "left join fetch m.attachments b " +
      "where m.channel.id = :channelId " +
      "and (cast(:cursor as java.time.Instant) is null or m.createdAt < :cursor) " +
      "order by m.createdAt desc, m.id desc")
  List<Message> findAllByChannel_Id(@Param("channelId")UUID channelId, @Param("cursor") Instant lastCreatedAt, Pageable pageable);


  long countByChannel_Id(UUID channelId);
}
