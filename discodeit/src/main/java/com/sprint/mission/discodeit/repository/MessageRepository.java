package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("select m from Message m " +
      "join fetch m.author a " +
      "left join fetch a.profile " +
      "where m.channel.id = :channelId")
  Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);
}
